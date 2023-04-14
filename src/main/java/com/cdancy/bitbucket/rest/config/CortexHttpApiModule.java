package com.cdancy.bitbucket.rest.config;


import com.cdancy.bitbucket.rest.BitbucketApi;
import com.cdancy.bitbucket.rest.handlers.BitbucketErrorHandler;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import org.jclouds.domain.Credentials;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.internal.FilterStringsBoundToInjectorByName;
import org.jclouds.json.config.GsonModule;
import org.jclouds.location.config.LocationModule;
import org.jclouds.proxy.ProxyConfig;
import org.jclouds.reflect.Invocation;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresHttpApi;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.binders.BindToJsonPayloadWrappedWith;
import org.jclouds.rest.config.SetCaller;
import org.jclouds.rest.internal.InvokeHttpMethod;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.rest.internal.TransformerForRequest;

import javax.inject.Named;
import javax.inject.Singleton;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.util.concurrent.Atomics.newReference;
import static org.jclouds.Constants.PROPERTY_PROXY_FOR_SOCKETS;
import static org.jclouds.reflect.Types2.checkBound;
import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

//@ConfiguresHttpApi
//public class CortexHttpApiModule extends CortexCustomHttpApiModule<BitbucketApi> {
//
//    @Override
//    protected void bindErrorHandlers() {
//        bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(BitbucketErrorHandler.class);
//        bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(BitbucketErrorHandler.class);
//        bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(BitbucketErrorHandler.class);
//    }
//}

//@ConfiguresHttpApi
class CortexCustomHttpApiModule<A> extends AbstractModule {
    protected final Class<A> api;

    protected final AtomicReference<AuthorizationException> authException = newReference();


    /**
     * Note that this ctor requires that you instantiate w/resolved generic
     * params. For example, via a subclass of a bound type, or natural
     * instantiation w/resolved type params.
     */
    @SuppressWarnings("unchecked")
    protected CortexCustomHttpApiModule() {
        this.api = Class.class.cast(checkBound(new TypeToken<A>(getClass()) {
            private static final long serialVersionUID = 1L;
        }).getRawType());
    }

    public CortexCustomHttpApiModule(Class<A> api) {
        this.api = api;
    }

    protected void installLocations() {
        install(new LocationModule());
    }

    @Override
    protected void configure() {
        install(new SaxParserModule());
        install(new GsonModule());
        install(new SetCaller.Module());
        install(new FactoryModuleBuilder().build(BindToJsonPayloadWrappedWith.Factory.class));
        bind(new TypeLiteral<Function<HttpRequest, Function<HttpResponse, ?>>>() {
        }).to(TransformerForRequest.class);
        bind(new TypeLiteral<org.jclouds.Fallback<Object>>() {
        }).to(MapHttp4xxCodesToExceptions.class);
        bind(new TypeLiteral<Function<Invocation, HttpRequest>>() {
        }).to(RestAnnotationProcessor.class);
        bind(IdentityFunction.class).toInstance(IdentityFunction.INSTANCE);
        // this will help short circuit scenarios that can otherwise lock out users
        bind(new TypeLiteral<AtomicReference<AuthorizationException>>() {
        }).toInstance(authException);
        bind(new TypeLiteral<Function<Predicate<String>, Map<String, String>>>() {
        }).to(FilterStringsBoundToInjectorByName.class);
        System.out.println("hi addi custom proxy for URI");
        bind(new TypeLiteral<Function<URI, Proxy>>() {}).to(CortexProxyForURI.class);
        installLocations();

        bind(new TypeLiteral<Function<Invocation, Object>>() {
        }).to(InvokeHttpMethod.class);
        bindHttpApi(binder(), api);
        bindHttpApi(binder(), HttpClient.class);
        bindErrorHandlers();
        bindRetryHandlers();
    }

    /**
     * overrides this to change the default retry handlers for the http engine
     *
     * ex.
     *
     * <pre>
     * bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(AWSRedirectionRetryHandler.class);
     * bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(AWSClientErrorRetryHandler.class);
     * </pre>
     *
     */
    protected void bindRetryHandlers() {
    }

    /**
     * overrides this to change the default error handlers for the http engine
     *
     * ex.
     *
     * <pre>
     * bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseAWSErrorFromXmlContent.class);
     * bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseAWSErrorFromXmlContent.class);
     * bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseAWSErrorFromXmlContent.class);
     * </pre>
     *
     *
     */
    protected void bindErrorHandlers() {

    }
}

@Singleton
class CortexProxyForURI implements Function<URI, Proxy> {
    private final ProxyConfig config;

    @Inject(optional = true)
    @Named(PROPERTY_PROXY_FOR_SOCKETS)
    private boolean useProxyForSockets = true;

    @VisibleForTesting
    @Inject
    CortexProxyForURI(ProxyConfig config) {
        this.config = checkNotNull(config, "config");
    }

    /**
     * @param endpoint
     *           <ul>
     *           <li>http URI for http connections</li>
     *           <li>https URI for https connections</li>
     *           <li>ftp URI for ftp connections</li>
     *           <li>socket://host:port for tcp client sockets connections</li>
     *           </ul>
     */
    @Override
    public Proxy apply(URI endpoint) {
        System.out.println("hi addi inside apply" );
        if (!useProxyForSockets && "socket".equals(endpoint.getScheme())) {
            System.out.println("hi addi retrning early" );
            return Proxy.NO_PROXY;
        }

        System.out.println("hi addi inside apply proxy is " + config.getProxy() );
        System.out.println("hi addi inside apply proxy is present is " + config.getProxy().isPresent() );
        if (config.getProxy().isPresent()) {

            System.out.println("hi addi is present" );
            SocketAddress addr = new InetSocketAddress(config.getProxy().get().getHost(), config.getProxy().get()
                .getPort());
            Proxy proxy = new Proxy(config.getType(), addr);

            System.out.println("hi addi host is " +  config.getProxy().get().getHost());

            final Optional<Credentials> creds = config.getCredentials();

            System.out.println("hi addi creds is outside" +  creds.get().credential);
            if (creds.isPresent()) {

                System.out.println("hi addi creds is inside schaing the default credentials" +  creds.get().identity);
                Authenticator authenticator = new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(creds.get().identity, creds.get().credential.toCharArray());
                    }
                };
                Authenticator.setDefault(authenticator);
            }

            return proxy;
        }
        if (config.isJvmProxyEnabled()) {
            return getDefaultProxy(endpoint);
        }

        if (config.useSystem()) {
            // see notes on the Constant which initialized the above for deprecation;
            // in short the following applied after startup is documented to have no effect.
            System.setProperty("java.net.useSystemProxies", "true");
            return getDefaultProxy(endpoint);
        }
        return Proxy.NO_PROXY;
    }

    private Proxy getDefaultProxy(URI endpoint) {
        Iterable<Proxy> proxies = ProxySelector.getDefault().select(endpoint);
        return getLast(proxies);
    }

}

