import java.util.Arrays;
import java.util.List;

// My test for HW4. I followed the output example from the assignment PDF.
// I also check some values to make sure the methods work.
public class Test {
    private static int checks = 0;

    private static void check(Object actual, Object expected, String description) {
        checks++;
        if (!java.util.Objects.equals(actual, expected)) {
            throw new AssertionError(description + ": expected=" + expected + ", actual=" + actual);
        }
    }

    private static void show(String label, Object value) {
        System.out.println(label + value);
    }

    public static void main(String[] args) {
        MusicPlayer player = new MusicPlayer();

        System.out.println("--- 1. Build the playlist ---");
        String[][] songs = {
                {"s1", "Night Drive", "Artist 1"},
                {"s2", "Slow Tide", "Artist 2"},
                {"s3", "Copper Sky", "Artist 3"},
                {"s4", "Static Bloom", "Artist 4"},
                {"s5", "Low Orbit", "Artist 5"},
                {"s6", "Glass Rivers", "Artist 6"}
        };
        for (String[] s : songs) {
            boolean added = player.addSong(s[0], s[1], s[2]);
            check(added, true, "New song added");
            System.out.println(added);
        }
        boolean duplicate = player.addSong("s3", "Duplicate", "Other");
        check(duplicate, false, "Duplicate ID rejected");
        show("duplicate s3: ", duplicate);
        List<String> initialList = Arrays.asList(
                "Night Drive", "Slow Tide", "Copper Sky", "Static Bloom", "Low Orbit", "Glass Rivers");
        check(player.showPlaylist(), initialList, "Initial playlist order");
        show("playlist: ", player.showPlaylist());

        System.out.println("--- 2. Look up songs ---");
        check(player.findSong("s4").getTitle(), "Static Bloom", "Lookup s4");
        show("find s4: ", player.findSong("s4"));
        check(player.findSong("s9"), null, "Unknown song");
        show("find s9: ", player.findSong("s9"));

        System.out.println("--- 3. Play and move forward ---");
        show("play s1: ", player.play("s1"));
        check(player.nowPlaying().getId(), "s1", "Play s1");
        show("play s9: ", player.play("s9"));
        check(player.nowPlaying().getId(), "s1", "Invalid play leaves current unchanged");
        show("next: ", player.next());
        check(player.nowPlaying().getId(), "s2", "Playlist next s2");
        show("next: ", player.next());
        check(player.nowPlaying().getId(), "s3", "Playlist next s3");
        check(player.showHistory(), Arrays.asList("Slow Tide", "Night Drive"), "History is LIFO");
        show("history: ", player.showHistory());

        System.out.println("--- 4. Up next ---");
        // s1 goes first, and s5 and s6 go to the back.
        boolean q1 = player.queueNext("s1");
        boolean q5 = player.queueLater("s5");
        boolean q6 = player.queueLater("s6");
        boolean q9 = player.queueLater("s9");
        check(q1 && q5 && q6 && !q9, true, "Queue valid/invalid IDs");
        System.out.println(q1);
        System.out.println(q5);
        System.out.println(q6);
        show("queue s9: ", q9);
        check(player.showUpNext(), Arrays.asList("Night Drive", "Low Orbit", "Glass Rivers"), "Queue order");
        show("up next: ", player.showUpNext());
        show("next: ", player.next());
        show("now playing: ", player.nowPlaying());
        check(player.showHistory(), Arrays.asList("Copper Sky", "Slow Tide", "Night Drive"), "History after queue next");
        show("history: ", player.showHistory());

        System.out.println("--- 5. Back button ---");
        show("back: ", player.back());
        check(player.nowPlaying().getId(), "s3", "Back to s3");
        check(player.showUpNext(), Arrays.asList("Night Drive", "Low Orbit", "Glass Rivers"), "Current returned to front");
        show("up next: ", player.showUpNext());
        check(player.showHistory(), Arrays.asList("Slow Tide", "Night Drive"), "Back pops history");
        show("history: ", player.showHistory());

        System.out.println("--- 6. Remove songs ---");
        show("remove s5: ", player.removeSong("s5"));
        show("remove current (s3): ", player.removeSong("s3"));
        show("remove s9: ", player.removeSong("s9"));
        check(player.findSong("s5"), null, "Removed song absent from library");
        check(player.showPlaylist(), Arrays.asList("Night Drive", "Slow Tide", "Copper Sky", "Static Bloom", "Glass Rivers"), "Playlist after removal");
        show("playlist: ", player.showPlaylist());
        check(player.showUpNext(), Arrays.asList("Night Drive", "Glass Rivers"), "Removed queued song hidden");
        show("up next: ", player.showUpNext());

        System.out.println("--- 7. Removed songs are skipped ---");
        show("next: ", player.next());
        show("next: ", player.next());
        show("next at end: ", player.next());
        show("now playing: ", player.nowPlaying());
        show("back: ", player.back());
        show("up next: ", player.showUpNext());
        check(player.nowPlaying().getId(), "s1", "Back to s1");
        check(player.showUpNext(), Arrays.asList("Glass Rivers"), "Back requeues s6");

        System.out.println("--- 8. Play counts ---");
        check(player.playCount("s1"), 4, "s1 play count");
        check(player.playCount("s3"), 2, "s3 play count");
        check(player.playCount("s6"), 1, "s6 play count");
        check(player.playCount("s5"), 0, "Removed song play count forgotten");
        show("s1: ", player.playCount("s1"));
        show("s3: ", player.playCount("s3"));
        show("s6: ", player.playCount("s6"));
        show("s5 (removed): ", player.playCount("s5"));

        System.out.println("--- 9. Empty player ---");
        MusicPlayer empty = new MusicPlayer();
        check(empty.next(), null, "Empty next");
        check(empty.back(), null, "Empty back");
        check(empty.nowPlaying(), null, "Empty nowPlaying");
        check(empty.showPlaylist(), java.util.Collections.emptyList(), "Empty playlist");
        show("next: ", empty.next());
        show("back: ", empty.back());
        show("now playing: ", empty.nowPlaying());
        show("playlist: ", empty.showPlaylist());

        // If a check failed, the program would stop with an error.
    }
}
