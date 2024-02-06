package com.asteral.asteralserver;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Objects;

public class Review implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String poster;
    private String comment;
    private int star;

    public Review(String poster, String comment, int star) {
        this.poster = poster;
        this.comment = comment;
        this.star = star;
    }

    public String getPoster() {
        return poster;
    }

    public String getComment() {
        return comment;
    }

    public int getStar() {
        return star;
    }


    public void setStar(int star) {
        this.star = star;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Review && ((Review) obj).poster.equals(this.poster);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.poster);
        return hash;
    }
    
    public static Review readReviewFromStream(Socket ascteralSocket) {
        Review review = null;
        try (ObjectInputStream in = new ObjectInputStream(ascteralSocket.getInputStream())) {
            review = (Review) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return review;
    }
    
    public static Review readReviewFromLine(String line, int offset) {
        String[] parts = line.split("\t");
        return new Review(parts[offset], parts[offset+1], Integer.parseInt(parts[offset+2]));
    }
    
    @Override
    public String toString() {
        return poster + "\t" + comment + "\t" + star;
    }
}
