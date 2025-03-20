### Version 3.1.2 (TBA)

### Version 3.1.1 (12/4/2023)
* ADDED: new API `CompareApi` . - [PR 411](https://github.com/cdancy/bitbucket-rest/pull/411)

### Version 3.1.0 (1/17/2023)
* ADDED: `commitSummaries` field to `MergeConfig` model . - [PR 365](https://github.com/cdancy/bitbucket-rest/pull/365)

### Version 3.0.2 (4/26/2022)
* Bump jclouds to latest released version

### Version 3.0.1 (11/9/2021)
* Merge lots of long overdue PR's

### Version 3.0.0 (11/1/2021)
* Migrate to maven central

### Version 2.7.2 (02/08/2021)
* ADDED: `Commit` object gained properties `committer` and `committerTimestamp` . - [PR 261](https://github.com/cdancy/bitbucket-rest/pull/261)
* ADDED: `PullRequestSettings` object gained property `unapproveOnUpdate` . - [PR 263](https://github.com/cdancy/bitbucket-rest/pull/263)

### Version 2.7.1 (10/30/2020)
* ADDED: `SearchApi` with endpoints `search` . - [PR 242](https://github.com/cdancy/bitbucket-rest/pull/242)

### Version 2.7.0 (9/29/2020)
* ADDED: `KeysApi` with endpoints `listByRepo, createForRepo, getForRepo, deleteFromRepo, listByProject, createForProject, getForProject, deleteFromProject` . - [PR 238](https://github.com/cdancy/bitbucket-rest/pull/238)

### Version 2.6.3 (7/27/2020)
* ADDED: `MergeConfigType.PROJECT`. - [PR 228](https://github.com/cdancy/bitbucket-rest/pull/228)

### Version 2.6.2 (12/18/2019)
* ADDED: Bumped `autovalue` to `1.7`. - [PR 207](https://github.com/cdancy/bitbucket-rest/pull/207)
* ADDED: Bumped `gradle` to `5.6.2`. - [PR 207](https://github.com/cdancy/bitbucket-rest/pull/207)
* ADDED: Added compile dependency on `jaxb-api` to make library work with jdk12. - [PR 207](https://github.com/cdancy/bitbucket-rest/pull/207)
* ADDED: Use correct data formats in `InsightReportData` value. - [PR 209](https://github.com/cdancy/bitbucket-rest/pull/209)

### Version 2.6.1 (10/23/2019)
* BUG: Bumped `guice` dependency to account for classloader leak bug with older version of guice.
* ADDED: Bumped `jclouds` and `autovalue` dependencies.

### Version 2.6.0 (10/23/2019)
* ADDED: `InsightsApi`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `listAnnotations`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `listReports`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `getReport`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `createReport`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `deleteReport`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `deleteAnnotation`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `createAnnotation`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `getAnnotationsByReport`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)
* ADDED: `InsightsApi` gained endpoint `createAnnotations`. - [PR 205](https://github.com/cdancy/bitbucket-rest/pull/205)

### Version 2.5.3 (4/26/2019)
* ADDED: `RepositoryApi` gained endpoint `listAll`. - [PR 192](https://github.com/cdancy/bitbucket-rest/pull/192)

### Version 2.5.2 (4/2/2019)
* ADDED: Bump `gradle` to `4.10.3`.
* ADDED: `TasksApi` gained endpoint `update`. - [PR 188](https://github.com/cdancy/bitbucket-rest/pull/188)

### Version 2.5.1 (3/19/2019)
* ADDED: `FileApi` gained endpoint `lastModified`. - [PR 180](https://github.com/cdancy/bitbucket-rest/pull/180)
* REFACTOR: `FileApi.listFiles` gained optional `path` param. - [PR 187](https://github.com/cdancy/bitbucket-rest/pull/187)

### Version 2.5.0 (3/3/2019)
* ADDED: Bump `jclouds` to `2.1.2`.
* ADDED: `WebHookApi`. - [PR 176](https://github.com/cdancy/bitbucket-rest/pull/176)
* ADDED: `WebHookApi` gained endpoint `list`. - [PR 176](https://github.com/cdancy/bitbucket-rest/pull/176)
* ADDED: `WebHookApi` gained endpoint `get`. - [PR 176](https://github.com/cdancy/bitbucket-rest/pull/176)
* ADDED: `WebHookApi` gained endpoint `create`. - [PR 176](https://github.com/cdancy/bitbucket-rest/pull/176)
* ADDED: `WebHookApi` gained endpoint `update`. - [PR 176](https://github.com/cdancy/bitbucket-rest/pull/176)
* ADDED: `WebHookApi` gained endpoint `delete`. - [PR 176](https://github.com/cdancy/bitbucket-rest/pull/176)

### Version 2.4.3 (2/25/19)
* BUG: **BREAKING CHANGE** `SyncApi.synchronize` now returns `SyncState` object instead of `Reference`.

### Version 2.4.2 (2/16/19)
* ADDED: `FileApi` gained endpoint `updateContent`. - [PR 174](https://github.com/cdancy/bitbucket-rest/pull/174)

### Version 2.4.1 (2/16/19)
* ADDED: `CommentsApi` gained overloaded endpoint method for `fileComments` to take advantage of additional parameters. Previous version has been marked as deprecated. - [PR 171](https://github.com/cdancy/bitbucket-rest/pull/171)

### Version 2.4.0 (2/16/2019)
* BUG: If a task contains both a comment and a like then parsing would fail. - [PR 168](https://github.com/cdancy/bitbucket-rest/pull/168)
* ADDED: `SyncApi`. - [PR 166](https://github.com/cdancy/bitbucket-rest/pull/166)
* ADDED: `SyncApi` gained endpoint `enable`. - [PR 166](https://github.com/cdancy/bitbucket-rest/pull/166)
* ADDED: `SyncApi` gained endpoint `status`. - [PR 166](https://github.com/cdancy/bitbucket-rest/pull/166)
* ADDED: `SyncApi` gained endpoint `syncrhonize`. - [PR 166](https://github.com/cdancy/bitbucket-rest/pull/166)

### Version 2.3.0 (10/8/2018)
* ADDED: `ProjectApi` gained endpoint `createPermissionsByUser`. - [PR 160](https://github.com/cdancy/bitbucket-rest/pull/160)
* ADDED: `ProjectApi` gained endpoint `deletePermissionsByUser`. - [PR 160](https://github.com/cdancy/bitbucket-rest/pull/160)
* ADDED: `ProjectApi` gained endpoint `listPermissionsByUser`. - [PR 160](https://github.com/cdancy/bitbucket-rest/pull/160)
* ADDED: `ProjectApi` gained endpoint `createPermissionsByGroup`. - [PR 160](https://github.com/cdancy/bitbucket-rest/pull/160)
* ADDED: `ProjectApi` gained endpoint `deletePermissionsByGroup`. - [PR 160](https://github.com/cdancy/bitbucket-rest/pull/160)
* ADDED: `ProjectApi` gained endpoint `listPermissionsByGroup`. - [PR 160](https://github.com/cdancy/bitbucket-rest/pull/160)
* ADDED: Bump `shadow` plugin to `2.0.4`.
* ADDED: Bump `gradle` plugin to `4.10.2`.

### Version 2.2.1 (8/31/2018)
* ADDED: `BitbucketClient` exposes `modules` method on builder and constructor. - [PR 157](https://github.com/cdancy/bitbucket-rest/pull/157)

### Version 2.2.0 (8/20/2018)
* ADDED: `PullRequestApi` gained endpoint `delete`. - [PR 155](https://github.com/cdancy/bitbucket-rest/pull/155)
* REFACTOR: `BitbucketClient` now implements `Closeable` to better work with jdk8+ try-with-resources.
* ADDED: Bump `jclouds` to `2.1.1`.
* ADDED: Bump `AutoValue` to `1.6.2`
* ADDED: Bump `gradle-bintray-plugin` to `1.8.4`
* ADDED: Bump `gradle` to `4.9`

### Version 2.1.5 (7/10/2018)
* ADDED: `AdminApi` gained endpoint `createUser`. - [PR 145](https://github.com/cdancy/bitbucket-rest/pull/145)
* ADDED: `AdminApi` gained endpoint `deleteUser`. - [PR 145](https://github.com/cdancy/bitbucket-rest/pull/145)
* ADDED: `PullRequestApi` gained endpoint `addParticipant`. - [PR 145](https://github.com/cdancy/bitbucket-rest/pull/145)

### Version 2.1.4 (5/7/2018)
* BUG: Fix nullable description in `Status` domain object. - [PR 143](https://github.com/cdancy/bitbucket-rest/pull/143)

### Version 2.1.3 (5/4/2018)
* ADDED: Bump gradle to `4.7` - [Commit 1313c](https://github.com/cdancy/bitbucket-rest/commit/1313cecfef24ac4e45ebf50c9f75cd43bb76dad8)
* BUG: Fix to account for Bitbucket not handing back a list of `error` objects. - [PR 141](https://github.com/cdancy/bitbucket-rest/pull/141)

### Version 2.1.2 (3/31/2018)
* ADDED: Bump gradle to `4.6` - [PR 139](https://github.com/cdancy/bitbucket-rest/pull/139)
* ADDED: Bump jclouds to `2.1.0` - [PR 138](https://github.com/cdancy/bitbucket-rest/pull/138)
* ADDED: `BranchApi` gained endpoint `info` - [PR 137](https://github.com/cdancy/bitbucket-rest/pull/137)

### Version 2.1.1 (3/15/2018)
* ADDED: Bump gradle to `4.5` - [Commit abd64d](https://github.com/cdancy/bitbucket-rest/commit/abd64deedf09adb03e5873a6a5429a1b150e2c08)
* REFACTOR: Changed `AutoValue` to be a `compileOnly` dependency as it is not required at runtime - [Commit 27820](https://github.com/cdancy/bitbucket-rest/commit/2782085842cdf4b7c1464f69fc102a7aaf9747d1)

### Version 2.1.0 (2/2/2018)
* ADDED: `TagApi` gained endpoint `delete` - [PR 135](https://github.com/cdancy/bitbucket-rest/pull/135)
* ADDED: `TagApi` gained endpoint `list` - [PR 132](https://github.com/cdancy/bitbucket-rest/pull/132)
* ADDED: `HookApi` gained endpoints `update` and `settings` - [Commit 68cac5](https://github.com/cdancy/bitbucket-rest/commit/68cac5eda1ed81c01515420d62fb08a0582cf550)
* BREAKING: all "hook" related endpoints were moved out of the `RepositoryApi` class and were given their own dedicated `HookApi` class. - [Commit 68cac5](https://github.com/cdancy/bitbucket-rest/commit/68cac5eda1ed81c01515420d62fb08a0582cf550)

### Version 2.0.0 (1/21/2018)
* ADDED: Token (a.k.a Bearer) authentication support - [PR 122](https://github.com/cdancy/bitbucket-rest/pull/122)
* ADDED: Bump gradle to `4.4.1` - [Commit b8620](https://github.com/cdancy/bitbucket-rest/commit/99c0cab7a60175348997c547fda666e920fca09e)
* ADDED: Expose `jclouds` `overrides` option to BitbucketClient via builder, sys-props, or env-vars - [PR 123](https://github.com/cdancy/bitbucket-rest/pull/123)
* ADDED: Bump `jclouds` to `2.0.3` - [PR 124](https://github.com/cdancy/bitbucket-rest/pull/124)
* ADDED: Bump `shadow` plugin to `2.0.2` - [PR 125](https://github.com/cdancy/bitbucket-rest/pull/125)
* ADDED: Bump `auto-value` to `1.5.3` - [PR 126](https://github.com/cdancy/bitbucket-rest/pull/126)
* BREAKING: with this release various constructors were removed in the BitbucketClient object in an effort to simplify things. All apis and endpoints however remain the same.

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
