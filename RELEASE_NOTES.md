### Version 0.0.11 (TBA)
* ADDED: PagingApi - [PR 19](https://github.com/cdancy/bitbucket-rest/pull/19)
* ADDED: Bumped jclouds deps to 2.0.0 - [PR 20](https://github.com/cdancy/bitbucket-rest/pull/20)
* REFACTOR: Code cleanup and bump travis jdk to 8 for proper http DELETE method support - [PR 21](https://github.com/cdancy/bitbucket-rest/pull/21)
* ADDED: editorconfig - [PR 22](https://github.com/cdancy/bitbucket-rest/pull/22)

### Version 0.0.10 (8/26/2016)
* ADDED: CommentsApi gained endpoint `comment`, `createComment`, and `get` - [PR 16](https://github.com/cdancy/bitbucket-rest/pull/16)
* ADDED: CommentsApi gained endpoint `delete` - [PR 17](https://github.com/cdancy/bitbucket-rest/pull/17)

### Version 0.0.9 (8/26/2016)
* REFACTOR: Disabled OkHttp client supplier - [Commit 5c64](5c641b2f5a38abb36ba67e2e20720a6f634b760e)

### Version 0.0.8 (8/26/2016)
* REFACTOR: Nullable annotation added to `Path.extension` - [Commit 8ef4](8ef42312a4bedf2e5c1e6ec6f63f5c82e63b4563)

### Version 0.0.7 (8/10/2016)
* ADDED: PullRequestApi endpoint `commits` - [Commit feb7](feb70bf3fdfcdca23b82360e6fa9e441360e923c)

### Version 0.0.6 (8/9/2016)
* Bug fix release for malformed artifacts - [Commit 4aeb](4aebcaade85cd9c73a09ac9ea061a82b4b45e7f6)

### Version 0.0.5 (N/A) (DON'T USE)
* ADDED: eclipse configuration - [Pull Request 8](https://github.com/cdancy/bitbucket-rest/pull/8)
* ADDED: avoid star import and force checkstyle to `src/main/java` to avoid generated source - [Pull Request 10](https://github.com/cdancy/bitbucket-rest/pull/10)
* REFACTOR: remove unused variables and imports - [Pull Request 14](https://github.com/cdancy/bitbucket-rest/pull/14)
* ADDED: PullRequestApi endpoint `changes` - [Pull Request 15](https://github.com/cdancy/bitbucket-rest/pull/15)

### Version 0.0.4 (6/26/16)

* ADDED: BranchApi endpoint `delete` - [Commit 6833](https://github.com/cdancy/bitbucket-rest/commit/68338ac2a196b0c363da900e8b18ff758f45b7aa)
* ADDED: BranchApi endpoint `model` - [Commit 057d](https://github.com/cdancy/bitbucket-rest/commit/057d68fb04ff13e9d1d8c286174bd8fea8577d05)
* BUG: `role` and `status` properties of `Person` can be null on older version of bitbucket - [Commit abd9](https://github.com/cdancy/bitbucket-rest/commit/abd911916c58056fc62de056dc791245bf306b15)

### Version 0.0.3 (6/23/16)

* ADDED: basic travis-ci support - [Commit 48c4](https://github.com/cdancy/bitbucket-rest/commit/48c407b897e48b376f3fa88b02f797a347e0f376)
* ADDED: checkstyle plugin - [Commit 6b1f](https://github.com/cdancy/bitbucket-rest/commit/6b1fcf842bb87a2053b6d97ea81bc7aaf98c1317)
* ADDED: BranchApi with endpoints `create`, `getDefault`, and `updateDefault` - [Commit cd70](https://github.com/cdancy/bitbucket-rest/commit/cd70952f4a83f910b25e56da0e8cc4befb9283e8)
* ADDED: TagApi with endpoints `create` and `get` - [Commit 0553](https://github.com/cdancy/bitbucket-rest/commit/055397b3b46779354b9d8b26fac314d28e905dd5)

### Version 0.0.2 (6/21/16)

* ADDED: PullRequestApi endpoints `merge`, `decline`, and `reopen` - [Commit 6abf](https://github.com/cdancy/bitbucket-rest/commit/6abf1c2d291ef0e4093d57affbe33099dd9be668)
* ADDED: PullRequestApi endpoint `create` - [Commit dba6](https://github.com/cdancy/bitbucket-rest/commit/dba66c524173b6d2928e7be21e84b603fc052a15)
* ADDED: PullRequestApi endpoint `canMerge` - [Commit 5821](https://github.com/cdancy/bitbucket-rest/commit/5821d6a2c53abae2a74bb88c50e31cbb1f429469)
* ADDED: ProjectApi with endpoint `create` - [Commit 9c02](https://github.com/cdancy/bitbucket-rest/commit/9c027ea90f87ef055189c322b23d054f91949c80)
* ADDED: ProjectApi endpoint `get` - [Commit 0d02](https://github.com/cdancy/bitbucket-rest/commit/0d02095dbadcc405136ba96fc9ba84ae21054bac)
* ADDED: ProjectApi endpoint `delete` - [Commit b10d](https://github.com/cdancy/bitbucket-rest/commit/b10df1291119e09e54e5003cf3d49359e1001512)
* ADDED: RepositoryApi with endpoints `create`, `get`, and `delete` - [Commit 0b61](https://github.com/cdancy/bitbucket-rest/commit/0b615e32fef40f4503e16e7035a8453ce3ad6169)
* ADDED: Will now publish fat/shadow jar - [Commit a9e6](https://github.com/cdancy/bitbucket-rest/commit/a9e69dea188a8675dcf1e0d2973a963b5a67d5fa)

### Version 0.0.1 (5/20/16)

* project init
