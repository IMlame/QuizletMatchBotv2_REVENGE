import java.text.Normalizer;
import java.util.Hashtable;

public class test {

	public static void main(String[] args) {
		
		String dab = "ñ";
		dab = Normalizer.normalize(dab, Normalizer.Form.NFD).toString();
		System.out.println(dab);
		int dab2 = dab.charAt(1);
		System.out.println(dab2);
	}

}