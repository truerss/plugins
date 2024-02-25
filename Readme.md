## Release cycle
- Develop your feature in the branch
- Create PR and merge to master
- If it's time to publish new binaries - push new tag of pattern `vX.Y.Z`. It will be picked up by [sbt-ci-release](https://github.com/sbt/sbt-ci-release) and pushed to Sonatype

### base

Base Plugin for all TrueRSS plugins.

### truerss-reddit-imageviewer-plugin

Images from imgur or gfycat in feed

Configuration:

```
RedditImageViewerPlugin {
  r = [gifs, diy, EducationalGifs]
}
```


### truerss-stackoverflow-plugin

Read atom feeds from stackexchange sites.

Configuration:

```
StackoverflowPlugin {
  onlyQuestion = false
  onlyBestAnswer = false
  allAnswers = true
}
```

### truerss-tumblr-plugin

Read Tumblr posts

Configuration:

```config
TumblrPlugin = {
  consumer_key: "your-consumer-key"
}
```

### truerss-notifier-plugin

Push notification about new entries.

```
TrueRSSNotifierPlugin {
  position = "top-right" // top-left, top-right, center, bottom-left, bottom-right 
  dark = false           // or true
}
```

### truerss-youtube-plugin (moved to truerss main repo)

Embed Youtube Video.

Configuration: none
