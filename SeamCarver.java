package seamcarving;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public interface SeamCarver {

    /** Returns the current image. (This updates whenever a seam is removed.) */
    Picture picture();

    /** Sets the current image. */
    void setPicture(Picture picture);

    /** Returns the width of the current image, in pixels. */
    int width();

    /** Returns the height of the current image, in pixels. */
    int height();

    /** Returns the color of pixel (x, y) in the current image. */
    Color get(int x, int y);

    /** Returns the energy of pixel (x, y) in the current image. */
    default double energy(int x, int y) {
        int right = x + 1;
        int left = x - 1;
        int down = y + 1;
        int top = y - 1;
        if (!inBounds(x, y)){
            throw new IndexOutOfBoundsException();
        } else {

            if (x == 0){
                left = width() - 1;
            } 
            if (x == width() - 1){
                right = 0;
            }
            if (y == 0){
                top = height() - 1;
            }
            if (y == height() - 1){
                down = 0;
            }
            Color rightNeighbor = get(right, y);
            Color leftNeighbor = get(left, y);
            Color topNeighbor = get(x, top);
            Color downNeighbor = get(x, down);

            double rX = Math.pow(rightNeighbor.getRed()-leftNeighbor.getRed(), 2);
            double gX = Math.pow(rightNeighbor.getGreen()-leftNeighbor.getGreen(), 2);
            double bX = Math.pow(rightNeighbor.getBlue()-leftNeighbor.getBlue(), 2);
            double xGradient =  rX + gX + bX;

            double rY = Math.pow(downNeighbor.getRed()-topNeighbor.getRed(), 2);
            double gY = Math.pow(downNeighbor.getGreen()-topNeighbor.getGreen(), 2);
            double bY = Math.pow(downNeighbor.getBlue()-topNeighbor.getBlue(), 2);
            double yGradient =  rY + gY + bY;

            return Math.sqrt(xGradient + yGradient);
        }



    }

    /** Returns true iff pixel (x, y) is in the current image. */
    default boolean inBounds(int x, int y) {
        return (x >= 0) && (x < width()) && (y >= 0) && (y < height());
    }

    /**
     * Calculates and returns a minimum-energy horizontal seam in the current image.
     * The returned array will have the same length as the width of the image.
     * A value of v at index i of the output indicates that pixel (i, v) is in the seam.
     */
    int[] findHorizontalSeam();

    /**
     * Calculates and returns a minimum-energy vertical seam in the current image.
     * The returned array will have the same length as the height of the image.
     * A value of v at index i of the output indicates that pixel (v, i) is in the seam.
     */
    int[] findVerticalSeam();

    /** Calculates and removes a minimum-energy horizontal seam from the current image. */
    default void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("Input seam array cannot be null.");
        } else if (width() == 1) {
            throw new IllegalArgumentException("Image width is 1.");
        } else if (seam.length != width()) {
            throw new IllegalArgumentException("Seam length does not match image width.");
        }

        for (int i = 0; i < seam.length - 2; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException(
                        "Invalid seam, consecutive vertical indices are greater than one apart.");
            }
        }

        Picture carvedPicture = new Picture(width(), height() - 1);
        /* Copy over the all indices besides the index specified by the seam */
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < seam[i]; j++) {
                carvedPicture.set(i, j, get(i, j));
            }

            for (int j = seam[i] + 1; j < height(); j++) {
                carvedPicture.set(i, j - 1, get(i, j));
            }
        }

        setPicture(carvedPicture);
    }

    /** Calculates and removes a minimum-energy vertical seam from the current image. */
    default void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new NullPointerException("Input seam array cannot be null.");
        } else if (height() == 1) {
            throw new IllegalArgumentException("Image height is 1.");
        } else if (seam.length != height()) {
            throw new IllegalArgumentException("Seam length does not match image height.");
        }

        for (int i = 0; i < seam.length - 2; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException(
                        "Invalid seam, consecutive horizontal indices are greater than one apart.");
            }
        }

        Picture carvedPicture = new Picture(width() - 1, height());
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < seam[i]; j++) {
                carvedPicture.set(j, i, get(j, i));
            }

            for (int j = seam[i] + 1; j < width(); j++) {
                carvedPicture.set(j - 1, i, get(j, i));
            }
        }

        setPicture(carvedPicture);
    }
}
