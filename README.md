# PopularMoviesApp

An Android app, that helps you discover popular movies. Project 1 & 2 of Udacity Android Developer Nanodegree.

## Project 2
<img src="art/mobile_portrait_grid.png?raw=true" width="300">
<img src="art/mobile_portrait_detail.png?raw=true" width="300">

## Usage:
This app uses [The Movie Database API][1] to retrieve movies. You must provide your own API key in order to build the app. 

When you get it, just paste it to:  `app/key.preferences` as follows (without quotes):
```
movieDbKey = "your_v3_api_key_here" 
```
## Features
- Discover the most popular and the highest rated  movies
- Endless scrolling
- Watch movie trailers and teasers
- Read reviews from other users
- Mark movies as favourites
- Check out your favourites even when offline
- Material design
- UI optimized for landscape and portrait orientation
- Minimum SDK version: 15
  
## Screnshots

### Portrait:

<img src="art/mobile_portrait_detail.png?raw=true" width="300">
<img src="art/mobile_portrait_detail_cast.png?raw=true" width="300">

### Landscape:

<img src="art/mobile_landscape_grid.png?raw=true" width="600">

<img src="art/mobile_landscape_detail.png?raw=true" width="600">

  
## Libraries used
- GSON - Deserializing JSON string to object
- Glide - Easy image loading with caching
- OkHttp3
- Fab lib by Clans - Fab menu
- ButterKnife - Easy view inflation
- Android Support Libraries: appcompat-v7, support-v4, design, cardview, recyclerview, constraint-layout

## License

    Copyright 2017 David Komorowicz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://www.themoviedb.org/documentation/api
