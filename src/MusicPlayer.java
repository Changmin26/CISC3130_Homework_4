import java.util.*;
// Name: Changmin Lee
// Course: CISC 3130
// Homework 4: Music Player
// Programming Language: Java
// IDE / Editor: IntelliJ IDEA

// Playlist: Custom doubly linked list
// Library and play counts: HashMap
// Up next and history: ArrayDeque

public class MusicPlayer {

    public static class Song {
        private final String id;
        private final String title;
        private final String artist;

        public Song(String id, String title, String artist) {
            this.id = id;
            this.title = title;
            this.artist = artist;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }

        @Override
        public String toString() {
            return title;
        }
    }

    // Each node knows the song before and after it.
    private static class Node {
        Song song;
        Node prev;
        Node next;

        Node(Song song) {
            this.song = song;
        }
    }

    private Node head;
    private Node tail;
    private Song current;

    private Map<String, Node> library = new HashMap<>();
    private Deque<String> upNext = new ArrayDeque<>();
    private Deque<String> history = new ArrayDeque<>();
    private Map<String, Integer> playCounts = new HashMap<>();

    public boolean addSong(String id, String title, String artist) {
        // Do not add the same song ID again.
        if (id == null || library.containsKey(id)) {
            return false;
        }

        Node newNode = new Node(new Song(id, title, artist));
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        library.put(id, newNode);
        return true;
    }

    public Song findSong(String id) {
        Node node = library.get(id);
        if (node == null) {
            return null;
        }
        return node.song;
    }

    public List<String> showPlaylist() {
        List<String> songs = new ArrayList<>();
        Node node = head;
        while (node != null) {
            songs.add(node.song.getTitle());
            node = node.next;
        }
        return songs;
    }

    public Song play(String id) {
        Song song = findSong(id);
        if (song == null) {
            return null;
        }

        if (current != null) {
            history.push(current.getId());
        }
        return startSong(song);
    }

    public Song nowPlaying() {
        return current;
    }

    public int playCount(String id) {
        return playCounts.getOrDefault(id, 0);
    }

    public boolean queueLater(String id) {
        if (!library.containsKey(id)) {
            return false;
        }
        upNext.addLast(id);
        return true;
    }

    public boolean queueNext(String id) {
        if (!library.containsKey(id)) {
            return false;
        }
        upNext.addFirst(id);
        return true;
    }

    public List<String> showUpNext() {
        List<String> songs = new ArrayList<>();
        for (String id : upNext) {
            Song song = findSong(id);
            if (song != null) {
                songs.add(song.getTitle());
            }
        }
        return songs;
    }

    public Song next() {
        // First find a song we can play. Some old IDs may be removed.
        Song nextSong = null;
        boolean fromQueue = false;

        for (String id : upNext) {
            nextSong = findSong(id);
            if (nextSong != null) {
                fromQueue = true;
                break;
            }
        }

        // If the queue has nothing, try the next song in the playlist.
        if (!fromQueue && current != null) {
            Node node = library.get(current.getId());
            if (node != null && node.next != null) {
                nextSong = node.next.song;
            }
        }

        // Don't change anything if there is no next song.
        if (nextSong == null) {
            return null;
        }

        if (fromQueue) {
            // Remove old IDs until we take the first available song.
            while (!upNext.isEmpty()) {
                String id = upNext.pollFirst();
                if (library.containsKey(id)) {
                    break;
                }
            }
        } else {
            upNext.clear();
        }

        if (current != null) {
            history.push(current.getId());
        }
        return startSong(nextSong);
    }

    public Song back() {
        Song previous = null;

        // Check history first before changing the current song.
        for (String id : history) {
            previous = findSong(id);
            if (previous != null) {
                break;
            }
        }
        if (previous == null) {
            return null;
        }

        // Skip songs that were already removed.
        while (!history.isEmpty()) {
            String id = history.pop();
            if (library.containsKey(id)) {
                break;
            }
        }

        if (current != null) {
            upNext.addFirst(current.getId());
        }
        return startSong(previous);
    }

    public List<String> showHistory() {
        List<String> songs = new ArrayList<>();
        for (String id : history) {
            Song song = findSong(id);
            if (song != null) {
                songs.add(song.getTitle());
            }
        }
        return songs;
    }

    public boolean removeSong(String id) {
        Node node = library.get(id);
        if (node == null) {
            return false;
        }
        if (current != null && current.getId().equals(id)) {
            return false;
        }

        // Connect the nodes around the song we remove.
        if (node.prev == null) {
            head = node.next;
        } else {
            node.prev.next = node.next;
        }

        if (node.next == null) {
            tail = node.prev;
        } else {
            node.next.prev = node.prev;
        }

        library.remove(id);
        playCounts.remove(id);
        return true;
    }

    // I call this whenever a song starts, so I don't forget its play count.
    private Song startSong(Song song) {
        current = song;
        playCounts.put(song.getId(), playCount(song.getId()) + 1);
        return song;
    }
}
