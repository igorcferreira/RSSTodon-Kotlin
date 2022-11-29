# RSSTodon

This is an experiment on Kotlin multiplatform using Compose for UI components.

## Modules

- **api:** Layer to communicate with [Mastodon API](https://docs.joinmastodon.org/api/) using [Ktor client](https://ktor.io/docs/create-client.html) and Kotlinx for [serialization](https://github.com/Kotlin/kotlinx.serialization) and [co-routines](https://github.com/Kotlin/kotlinx.coroutines).
- **ui:** [Compose multiplatform](https://www.jetbrains.com/lp/compose-mpp/) layer that uses the api module to login and post a toot.
- **android:** Android layer that uses the ui module to publish an Android application.
- **desktop:** [Compose Desktop](https://www.jetbrains.com/lp/compose-desktop/) layer that uses the ui module to publish a desktop app. At the moment, only macOS is fully configured.

## Running the app

Create a `local.properties` file similar to:

```properties
instance=https://mastodon.social
clientId=rjw6...2a8
clientSecret=ZDnE...xih4
scope=read write
redirectScheme=rsstodon
```

After that, sync the project and run either Android app or Desktop app.

> **Note:** You can create an app on your Mastodon account, in the "[Development](https://mastodon.social/settings/applications)" section. 
Once the app is created, you can copy the client id and client key from the profile. 
Remember that the scope and redirect scheme in your local.properties also needs to match the scope configured in the server.

### UI Sample

![Desktop app](docs/desktop.gif)
![Android app](docs/app.gif)

### License

[Apache v2.0](LICENSE)