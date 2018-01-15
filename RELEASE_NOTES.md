### Version 1.0.6 (TBA)
* ADDED: Token (a.k.a Bearer) authentication support - [PR 122](https://github.com/cdancy/bitbucket-rest/pull/122)

### Version 1.0.5 (12/8/2017)

* ADDED: Annotated `emailAddress` property of `User` object with `Nullable` - [Commit 3fd95](https://github.com/cdancy/bitbucket-rest/commit/3fd9551a6ec180bc57ccb4f491258dc1ca930ed1)
* ADDED: Bump gradle to `4.4` - [Commit b8620](https://github.com/cdancy/bitbucket-rest/commit/b86206ded64646f92ed0fac9ea85e411bbfae4d0)

### Version 1.0.4 (11/17/2017)
* ADDED: support for PMD - [Commit 8fd1c](https://github.com/cdancy/bitbucket-rest/commit/8fd1c565d47a94a49088fed0c8f12a391e5ce252)
* UPDATE: defalt to jdk 1.8 for building - [Commit 57cd639](https://github.com/cdancy/bitbucket-rest/commit/57cd639cd854bca8edda1f4fbc408e6832959b48)
* UPDATE: Bump logback to `1.2.3` - [Commit 01bc72](https://github.com/cdancy/bitbucket-rest/commit/01bc72b9d1f6a18eb9356d67f33e09da3970253b)
* UPDATE: Bump mock-webserver to `2.7.5` - [Commit 569ae](https://github.com/cdancy/bitbucket-rest/commit/569ae08b2e30d01f365f1d30959d936a77d228e1)
* UPDATE: Bump assertj-core to `3.8.0` - [Commit 75e3a4](https://github.com/cdancy/bitbucket-rest/commit/75e3a465807692f938d3c684de27c278449d0138)
* UPDATE: Bump auto-value to `1.5.1` - [Commit 8286702](https://github.com/cdancy/bitbucket-rest/commit/8286702aa17ba2dba22a04048e86f51b68d74da3)
* UPDATE: Bump gradle to `4.2.1` - [Commit a5171b](https://github.com/cdancy/bitbucket-rest/commit/a5171b1fdf7b503df80fc88e3497a5381cdc9e1b)
* REFACTOR: `CommitsApi.listFiles` gained additional optional parameters - [PR 117](https://github.com/cdancy/bitbucket-rest/pull/117)
* ADDED: Bump gradle to `4.3.1` - [PR 118](https://github.com/cdancy/bitbucket-rest/pull/118)
* ADDED: `FileApi` gained endpoint `listFiles` - [PR 119](https://github.com/cdancy/bitbucket-rest/pull/119)

### Version 1.0.3 (8/10/2017)
* FIX: Ensure `BitbucketFallbacks` properly parses `conflicted` and `vetoes` properties when constructing `Error` objects - [Commit 53908](https://github.com/cdancy/bitbucket-rest/commit/59ff2b30f209872d1ba23d52850d98201daf4cea)

### Version 1.0.2 (8/10/2017)
* ADDED: `Error` object gained properties `conflicted` and `vetoes` - [Commit 53908](https://github.com/cdancy/bitbucket-rest/commit/539081b531fffa7448cc27b69fc96a38043a907d)

### Version 1.0.1 (7/31/2017)
* FIX: remove debugging printlns that snuck in. No functionality added/removed.

### Version 1.0.0 (7/30/2017)
* **!!!!! BREAKING !!!!!**: All endpoints which previously returned a boolean will now return a `RequestStatus` object. This object has 2 parameters: `value` and `errors`. If the request was good `value` will be set to true and if the request was bad then `value` will be set to false. Additionally, and if the request was bad, the `errors` object will also be populated as per the standard set by this library.

* **!!!!! BREAKING !!!!!**: All branch "permission" domain objects and endpoints have been renamed to "restrictions".

### Version 0.9.1 (7/15/2017)
* ADDED: `TasksApi` was created with endpoint `create` - [PR 104](https://github.com/cdancy/bitbucket-rest/pull/104)
* REFACTOR: `Comments` variable `properties` is now of type <String, JsonElement> instead of <String, String> to account variable value being returned - [PR 106](https://github.com/cdancy/bitbucket-rest/pull/106)
* ADDED: Bump jclouds to version `2.0.2` - [PR 107](https://github.com/cdancy/bitbucket-rest/pull/107)
* ADDED: `TasksApi` gained endpoint `get` - [PR 108](https://github.com/cdancy/bitbucket-rest/pull/108)
* ADDED: `TasksApi` gained endpoint `delete` - [PR 109](https://github.com/cdancy/bitbucket-rest/pull/109)

### Version 0.9.0 (6/12/2017)
* ADDED: `Activities` pojo gained value `REOPENED` - [PR 88](https://github.com/cdancy/bitbucket-rest/pull/88)
* ADDED: `DefaultReviewersApi` gained endpoint `listConditions` - [PR 81](https://github.com/cdancy/bitbucket-rest/pull/81)
* ADDED: `AdminApi` gained endpoint `listUsers` - [PR 91](https://github.com/cdancy/bitbucket-rest/pull/91)
* REFACTOR: `AdminApi` renamed endpoint `listUserByGroup` to `listUsersByGroup` - [PR 91](https://github.com/cdancy/bitbucket-rest/pull/91)
* ADDED: `DefaultReviewersApi` gained endpoint `createCondition` - [PR 82](https://github.com/cdancy/bitbucket-rest/pull/82)
* ADDED: `DefaultReviewersApi` gained endpoint `updateCondition` - [PR 83](https://github.com/cdancy/bitbucket-rest/pull/83)
* ADDED: `DefaultReviewersApi` gained endpoint `deleteCondition` - [PR 84](https://github.com/cdancy/bitbucket-rest/pull/84)
* ADDED: `FileApi` with single endpoint `getContent` - [PR 96](https://github.com/cdancy/bitbucket-rest/pull/96)
* ADDED: `FileApi` gained endpoint `listlines` - [PR 97](https://github.com/cdancy/bitbucket-rest/pull/97)
* REFACTOR: `FileApi` renamed endpoint `getContent` to `raw` and now returns domain object `RawContent` - [Commit 03def](https://github.com/cdancy/bitbucket-rest/commit/03def8350e87e43bcd092bf9e21579f4e54a60c0)

### Version 0.0.16 (6/2/2017)
* ADDED: `RepositoryApi` gained endpoints `listPermissionsByGroup` - [PR 61](https://github.com/cdancy/bitbucket-rest/pull/61)
* ADDED: `RepositoryApi` gained endpoints `createPermissionsByGroup` and `deletePermissionsByGroup` - [PR 62](https://github.com/cdancy/bitbucket-rest/pull/62)
* ADDED: `RepositoryApi` gained endpoints `listPermissionsByUser` - [PR 63](https://github.com/cdancy/bitbucket-rest/pull/63)
* ADDED: `RepositoryApi` gained endpoints `createPermissionsByUser` and `deletePermissionsByUser` - [PR 64](https://github.com/cdancy/bitbucket-rest/pull/64)
* ADDED: `RepositoryApi` gained endpoints `getPullRequestSettings` and `updatePullRequestSettings` - [PR 65](https://github.com/cdancy/bitbucket-rest/pull/65)
* ADDED: `RepositoryApi` gained endpoints `listHooks`, `getHook`, `enableHook` and `disableHook` - [PR 66](https://github.com/cdancy/bitbucket-rest/pull/66)
* ADDED: `BranchApi` gained endpoints `getModelConfiguration` - [PR 68](https://github.com/cdancy/bitbucket-rest/pull/68)
* ADDED: `BranchApi` gained endpoints `updateModelConfiguration` - [PR 69](https://github.com/cdancy/bitbucket-rest/pull/69)
* REFACTOR: Upgrade to gradle 3.5 - [PR 75](https://github.com/cdancy/bitbucket-rest/pull/75)
* BUG: Fix incorrect pom file generation - [PR 77](https://github.com/cdancy/bitbucket-rest/pull/77)
* ADDED: Checkstyle module to catch windows based line-endings - [PR 78](https://github.com/cdancy/bitbucket-rest/pull/78)
* ADDED: Checkstyle module to catch author annotations - [PR 79](https://github.com/cdancy/bitbucket-rest/pull/79)
* ADDED: `Branch` model gained attribute `metadata`  - [PR 72](https://github.com/cdancy/bitbucket-rest/pull/72)
* ADDED: `CommitsApi` gained endpoint `list` - [PR 85](https://github.com/cdancy/bitbucket-rest/pull/85)
* ADDED: `BuildStatusApi` gained endpoint `add` - [PR 86](https://github.com/cdancy/bitbucket-rest/pull/86)
* REFACTOR: All live tests can now be run successfully OOTB - [PR 87](https://github.com/cdancy/bitbucket-rest/pull/87)
* ADDED: `BranchApi` gained endpoint `deleteModelConfiguration` - [PR 70](https://github.com/cdancy/bitbucket-rest/pull/70)

### Version 0.0.15 (4/28/2017)
* ADDED: `PullRequestApi` gained endpoints `listActivities`, `listParticipants`, `assignParticipant`, and `deleteParticipant`. - [PR 55](https://github.com/cdancy/bitbucket-rest/pull/55)
* ADDED: new api `AdminApi` with single endpoint `listUserByGroup`. - [PR 57](https://github.com/cdancy/bitbucket-rest/pull/57/files)
* ADDED: `changes` endpoint to `CommitsApi`. - [PR 58](https://github.com/cdancy/bitbucket-rest/pull/58)
* ADDED: new api `BuildStatusApi` with endpoints `status` and `summary`. - [PR 59](https://github.com/cdancy/bitbucket-rest/pull/59)

### Version 0.0.14 (3/13/2017)
* ADDED: `Documentation` annotation to all endpoints. The idea is that this annotation will hold a list of links where the user can go to get more detailed information on the endpoint and it's options.
* ADDED: Bump jclouds, auto*, testng, assertj, and logback dependencies.
* ADDED: `comments` endpoint to `PullRequestApi`. - [PR 45](https://github.com/cdancy/bitbucket-rest/pull/45)
* ADDED: convert all `testng` assertions to `assertj`. - [PR 46](https://github.com/cdancy/bitbucket-rest/pull/46)
* ADDED: Fix various fallback implementations and added tests for each to catch these in the future.
* ADDED: Add new field in PullRequest bbject. - [PR 50](https://github.com/cdancy/bitbucket-rest/pull/50)
* ADDED: Add new api call for BranchPermission. - [PR 51](https://github.com/cdancy/bitbucket-rest/pull/51)
* ADDED: Add new attribute `AccessKey` for branchPermission. - [PR 53](https://github.com/cdancy/bitbucket-rest/pull/53)

### Version 0.0.13 (2/4/2017)
* ADDED: BranchApi gained proper page support. - [Commit 2c642c](https://github.com/cdancy/bitbucket-rest/commit/2c642c0736768649bd7fb0b6ed1f93b02d6d8f22)
* ADDED: CommitsApi with single `get` endpoint. - [Commit 5aca03](https://github.com/cdancy/bitbucket-rest/commit/5aca03d23521bf307973a3170f0760ce0d143f95)
* BREAKING: Domain class `Commit` was moved out of `pullrequest` package and into its own package. - [Commit 5aca03](https://github.com/cdancy/bitbucket-rest/commit/5aca03d23521bf307973a3170f0760ce0d143f95)
* ADDED: PullRequestApi gained proper page support through `list` endpoint. - [Commit 0474f58](https://github.com/cdancy/bitbucket-rest/commit/0474f58a1deceb0d1d6898487ef39561f2be7d17)
* REFACTOR: Code cleanup and minor refactorings to make overall API more consistent. - [Commit b4ff92](https://github.com/cdancy/bitbucket-rest/commit/b4ff92cf42b54be5a79c59f6c25928cd55884200)
* BREAKING: `ProjectApi.list` parameter order has been altered thus introducing a backwards incompatible change. `limit` and `start` parameters are now last in list to better align with overall project consistency.
* BUG: Fix packaging of all (e.g. shadow/shaded) jar so that it's actually usable. [Commit f2c0467](https://github.com/cdancy/bitbucket-rest/commit/f2c0467bc0cf2a776ac96e82f704b2d6ac065437)
* ADDED: `tests` jar for publication. - [Commit a24b99f](https://github.com/cdancy/bitbucket-rest/commit/a24b99fbd660b37b2d44a83821652f3953d7e2ea)

### Version 0.0.12 (12/29/2016)
* REFACTOR: re-model existing endpoints after new `page` design. - [PR 32](https://github.com/cdancy/bitbucket-rest/pull/32)
* BUG: remove use of *Holder objects for outgoing http traffic classes - [PR 33](https://github.com/cdancy/bitbucket-rest/pull/33)

### Version 0.0.11 (12/27/2016) *** DO NOT USE ***
* ADDED: PagingApi - [PR 19](https://github.com/cdancy/bitbucket-rest/pull/19)
* ADDED: Bumped jclouds deps to 2.0.0 - [PR 20](https://github.com/cdancy/bitbucket-rest/pull/20)
* REFACTOR: Code cleanup and bump travis jdk to 8 for proper http DELETE method support - [PR 21](https://github.com/cdancy/bitbucket-rest/pull/21)
* ADDED: default editorconfig - [PR 22](https://github.com/cdancy/bitbucket-rest/pull/22)
* REFACTOR: Factorize code with ErrorsHolder interface and Utils.nullToEmpty - [PR 25](https://github.com/cdancy/bitbucket-rest/pull/25)
* REFACTOR: Factorize Links attributes in domain classes with LinksHolder interface - [PR 26](https://github.com/cdancy/bitbucket-rest/pull/26)
* REFACTOR: Relocating external dependencies in fat jar - [PR 27](https://github.com/cdancy/bitbucket-rest/pull/27)

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
