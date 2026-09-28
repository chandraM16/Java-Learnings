package com.chandan.library;

import java.util.Objects;

enum MemberType {
    STUDENT(3), STAFF(10), GUEST(1);

    private final int maxLoans;

    MemberType(int maxLoans) {
        this.maxLoans = maxLoans;
    }

    int maxLoans() {
        return maxLoans;
    }
}

abstract class Member {
    protected final String id;
    protected int activeLoans;

    Member(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id is required");
        }
        this.id = id; // need this.id = id
    }

    abstract MemberType type();

    boolean canBorrow() {
        return activeLoans < type().maxLoans();
    }

    void borrow() {
        if (!canBorrow()) {
            throw new IllegalStateException(id + " has reached the loan limit");
        }
        activeLoans++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Member other = (Member) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return type() + " " + id + " (" + activeLoans + " loans)";
    }
}

class Student extends Member {
    Student(String id) {
        super(id);
    }

    @Override
    MemberType type() {
        return MemberType.STUDENT;
    }
}

class Staff extends Member {
    Staff(String id) {
        super(id);
    }

    @Override
    MemberType type() {
        return MemberType.STAFF;
    }
}

class Guest extends Member {
    Guest(String id) {
        super(id);
    }

    @Override
    MemberType type() {
        return MemberType.GUEST;
    }
}

public class LibraryApp {
    public static void main(String[] args) {
        int number = 1;
        Member s1 = new Student("S" + number);
        Member s2 = new Student("S1");
        Member staff = new Staff("T1");
        Member guest = new Guest("G1");

        System.out.println(s1.toString());
        System.out.println("same member: " + s1.equals(s2));

        for (int i = 0; i < 4; i++) {
            if (s1.canBorrow()) {
                s1.borrow();
            }
        }
        System.out.println("S1 loans: " + s1.activeLoans + ", T1 loans: " + staff.activeLoans);
        System.out.println(guest.toString());

        MemberType parsed = MemberType.valueOf(" staff ".strip().toUpperCase());
        System.out.println("Parsed: " + parsed + ", max loans " + parsed.maxLoans());
    }
}