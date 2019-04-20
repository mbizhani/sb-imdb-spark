# Search IMDb datasets via Spring Boot + Spark

The IMDb site provides subsets of IMDb data in his [site](https://www.imdb.com/interfaces/). A class diagram of the
data is depicted in the following picture.

![IMDbCD.png](/doc/IMDbCD.png)

Using Apache Spark inside Spring Boot, this sample project tris to answer following questions

- Search movies by "primaryTitle"
- Search actors by "primaryName"
- Search common-played movie(s) for two or more actors/actresses