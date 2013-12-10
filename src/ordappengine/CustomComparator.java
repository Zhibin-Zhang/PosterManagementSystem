package ordappengine;

import java.util.Comparator;

public class CustomComparator implements Comparator<BlobDate> {
	
    @Override
    public int compare(BlobDate o1, BlobDate o2) {
        return o1.getDate().compareTo(o2.getDate());
    }
}