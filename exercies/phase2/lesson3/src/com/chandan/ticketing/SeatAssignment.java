package com.chandan.ticketing;

import java.util.Objects;

public class SeatAssignment {
    private final char row;
    private final int seatNumber;

    public SeatAssignment(char row, int seatNumber) {

        if (row < 'A' || 'Z' < row) {
            throw new IllegalArgumentException("Row is out of range. (Valid Range; 'A' to 'Z')");

        }
        if (seatNumber < 1 || 50 < seatNumber) {
            throw new IllegalArgumentException("Seat no. is out of range. (Valid Range; 1 to 50)");
        }
        this.row = row;
        this.seatNumber = seatNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SeatAssignment seat = (SeatAssignment) o;
        return seatNumber == seat.seatNumber && row == seat.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, seatNumber);
    }

    @Override
    public String toString() {
        return row + "" + seatNumber;
    }

    public static void main(String[] args) {
        SeatAssignment s1 = new SeatAssignment('A', 34);
        SeatAssignment s2 = new SeatAssignment('A', 34);

        System.out.println(s1 == s2);
        System.out.println(s1.equals(s2));
        System.out.println(s1.toString());
        System.out.println(s1.hashCode());
        System.out.println(s2.hashCode());
    }
}
