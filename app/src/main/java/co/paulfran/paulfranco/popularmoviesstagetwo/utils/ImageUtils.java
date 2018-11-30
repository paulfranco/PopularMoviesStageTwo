package co.paulfran.paulfranco.popularmoviesstagetwo.utils;

import co.paulfran.paulfranco.popularmoviesstagetwo.Constants;

public class ImageUtils {

    // Constructor
    private ImageUtils() {
    }

    public static String buildBackdropImageUrl(String backdropPath, double holderWidth) {

        String imageWidth;

        if (holderWidth > 780) {
            imageWidth = "original";
        } else if (holderWidth > 500) {
            imageWidth = "w780";
        } else if (holderWidth > 342) {
            imageWidth = "w500";
        } else if (holderWidth > 185) {
            imageWidth = "w342";
        } else if (holderWidth > 154) {
            imageWidth = "w185";
        } else if (holderWidth > 92) {
            imageWidth = "w154";
        } else {
            imageWidth = "w92";
        }


        return Constants.TMDB_IMAGE_URL
                + imageWidth
                + "/"
                + backdropPath;
    }

    public static String buildPosterImageUrl(String posterPath, double holderWidth) {
        String imageWidth;

        if (holderWidth > 500) {
            imageWidth = "w342";
        } else {
            imageWidth = "w185";
        }

        return Constants.TMDB_IMAGE_URL
                + imageWidth
                + "/"
                + posterPath;
    }
}
