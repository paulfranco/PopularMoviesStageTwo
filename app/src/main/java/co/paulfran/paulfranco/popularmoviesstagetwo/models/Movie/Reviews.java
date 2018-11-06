package co.paulfran.paulfranco.popularmoviesstagetwo.models.Movie;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Reviews implements Parcelable {
    @JsonProperty("page")
    private int page;
    @JsonProperty("total_pages")
    private int totalPages;
    @JsonProperty("results")
    private List<Review> results;
    @JsonProperty("total_results")
    private int totalResults;

    public Reviews() {
        this.page = 0;
        this.totalPages = 0;
        this.results = new ArrayList<>();
        this.totalResults = 0;
    }

    // Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.page);
        dest.writeInt(this.totalPages);
        dest.writeTypedList(this.results);
        dest.writeInt(this.totalResults);
    }

    protected Reviews(Parcel in) {
        this.page = in.readInt();
        this.totalPages = in.readInt();
        this.results = in.createTypedArrayList(Review.CREATOR);
        this.totalResults = in.readInt();
    }

    public static final Parcelable.Creator<Reviews> CREATOR = new Parcelable.Creator<Reviews>() {
        @Override
        public Reviews createFromParcel(Parcel source) {
            return new Reviews(source);
        }

        @Override
        public Reviews[] newArray(int size) {
            return new Reviews[size];
        }
    };

    // Getters
    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public List<Review> getResults() {
        return results;
    }

    public int getTotalResults() {
        return totalResults;
    }

}