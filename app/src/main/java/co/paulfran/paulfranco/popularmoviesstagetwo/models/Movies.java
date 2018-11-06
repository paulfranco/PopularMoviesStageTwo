package co.paulfran.paulfranco.popularmoviesstagetwo.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movies implements Parcelable {
    @JsonProperty("page")
    private Integer page;
    @JsonProperty("results")
    private List<MovieResults> results;

    public Movies() {
        this.page = 0;
        this.results = new ArrayList<>();
    }

    // Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.page);
        dest.writeTypedList(this.results);
    }

    protected Movies(Parcel in) {
        this.page = (Integer) in.readValue(Integer.class.getClassLoader());
        this.results = in.createTypedArrayList(MovieResults.CREATOR);
    }

    public static final Parcelable.Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel source) {
            return new Movies(source);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    // Getters & Setters
    public Integer getPage() {
        return page;
    }

    public List<MovieResults> getResults() {
        return results;
    }

    public void appendMovies(Movies movies) {
        this.page = movies.getPage();
        this.results.addAll(movies.getResults());
    }


}
