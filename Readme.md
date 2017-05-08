

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


### truerss-youtube-plugin

Embed Youtube Video.

Configuration: none