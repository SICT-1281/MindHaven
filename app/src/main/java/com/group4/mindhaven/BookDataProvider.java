package com.group4.mindhaven;

import java.util.ArrayList;
import java.util.List;

public class BookDataProvider {
    public static List<Book> getBooks() {
        List<Book> books = new ArrayList<>();
        books.add(new Book("As a Man Thinketh", R.drawable.as_a_man_thinkth,
                "https://wahiduddin.net/thinketh/as_a_man_thinketh.pdf", "James Allen"));
        books.add(new Book("Think and Grow Rich ", R.drawable.think_and_grow_rich,
                "https://apex.oracle.com/pls/apex/lonestar/r/files/static/v13Y/Think-And-Grow-Rich_2011-06.pdf",
                "Napoleon Hill"));
        books.add(new Book("Meditations", R.drawable.meditations,
                "https://classics.mit.edu/Antoninus/meditations.html", "Marcus Aurelius"));
        books.add(new Book("Self-Reliance", R.drawable.self_reliance,
                "https://www.owleyes.org/text/self-reliance", "Ralph Waldo Emerson"));
        books.add(new Book("The Prophet", R.drawable.the_prophet,
                "https://www.gutenberg.org/files/58585/58585-h/58585-h.htm", "Kahlil Gibran"));
        books.add(new Book("The Power of Positive Thinking", R.drawable.the_power_of_positive_thinking,
                "http://dickyricky.com/books/recovery/The%20Power%20of%20Positive%20Thinking%20-%20Norman%20Vincent%20Peale.pdf",
                "Norman Vincent Peale"));
        books.add(new Book("Man’s Search for Meaning", R.drawable.mans_searching_for_meaning,
                "https://www.goodreads.com/book/show/4069.Man_s_Search_for_Meaning", "Viktor E. Frankl"));
        books.add(new Book("Awaken the Giant Within", R.drawable.awaken_the_giant_within,
                "https://example.com/awaken_the_giant_within", "Anthony Robbins"));
        books.add(new Book("Mindset: The New Psychology of Success", R.drawable.mindset,
                "https://adrvantage.com/wp-content/uploads/2023/02/Mindset-The-New-Psychology-of-Success-Dweck.pdf",
                "Carol S. Dweck"));
        books.add(new Book("The Gifts of Imperfection", R.drawable.gifts_of_imperfection,
                "https://www.hazelden.org/HAZ_MEDIA/2545_GiftsofImperfection.pdf", "Brené Brown"));
        books.add(new Book("The Four Agreements", R.drawable.the_four_agreements,
                "https://media.cmsmax.com/6j0m187z6e9h2yqesif6w/the-four-agreements-by-don-miguel-ruiz.pdf",
                "Don Miguel Ruiz"));

        return books;
    }
}