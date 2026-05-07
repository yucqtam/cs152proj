# Playlist Personalizer Generator

## Overview
A Java-based application that generates personalized music playlists based on user preferences. 
The program uses a local music dataset and applies filtering, scoring, and recommendation logic 
to generate playlists that match selected genres, artists, and explicit-content settings.

## Features
- Loads songs from a CSV music dataset
- Allows users to select preferred genres and artists
- Filters out blocked artists, blocked genres, and explicit songs if disabled
- Generates playlists using multiple recommendation engines
- Uses weighted scoring to rank songs based on genre and artist matches
- Provides a GUI for user interaction

## Recommendation Engines
### Engine 1: Genre Diversity
Engine 1 focuses on creating a playlist with a wider range of genres. 
It groups songs by preferred genres and tries to avoid overloading the 
playlist with only one style of music.

### Engine 2: Balanced Ranking
Engine 2 gives equal importance to genre and artist preferences. It scores 
songs based on how well they match the user’s selected genres and artists, 
then ranks the songs by total score.
