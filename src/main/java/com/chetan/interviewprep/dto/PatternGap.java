package com.chetan.interviewprep.dto;

// A "record" is Java's shorthand for an immutable data holder - it auto-generates
// the constructor, getters, equals/hashCode, and toString, so we don't have to
// write all that boilerplate for a class this simple.
public record PatternGap(String pattern, long questionCount) {
}
