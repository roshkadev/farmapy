package mobi.roshka.farmapy.utils;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public final class Crypto {

	// test
	//public static final String CLIENT_SECRET = "6KSE3aN13jpfVPkH4+JOgssSMab69DofW0bfMhv/FPbder2JqgQIEtf02QquU6ojpGMa9leJeSihT/ovBppIfLTO43PmqLDm9b3fIpxD4+DOFSCOz1+jFNXx3NEVA0UzvukzIu2J/u11s1sm2mOloqakVN7tkIuUk4mkFytfkRPbfpY1BTL9snv+ChlMg85eicOcSgrg42SFOoMkUdrmcCslnrJREy+zpvK+yqmJMujwWfPq6EfEs7hGgt/btKdz9zaq72vLDOY=";
	// 1.1.0
	public static final String CLIENT_SECRET = "r1XnQFFzfGhbpeCaLVVO763T8Vo9ImjMKeiyv0J6OGPKHueRSH4EmvnVIkIf42BpmO2jOIBnEtBiXQfXJ_5SeW3YAJomOx6zCtwoj91HF4833lQYt7qilagzetvKcHPvegeWQv4GS-1RTQmw1MXq7p07-YnWVtfdqwr-U6N6Q-cR6-8MRnxBH02OXQEA7Okkyrhx08lSWtbUiRiKHUt3TtDZgjqRsAq1_IHJQyY69mLPbSJpcyhYxwaJATwYHFh3Te6Nsb6Hs_E=";
	

    private static String convertToHex(byte[] data) { 
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 

    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
    	MessageDigest md;
    	md = MessageDigest.getInstance("SHA-1");
    	byte[] sha1hash = new byte[40];
    	md.update(text.getBytes("iso-8859-1"), 0, text.length());
    	sha1hash = md.digest();
    	return convertToHex(sha1hash);
    } 
	
//	public static String getHexSha1(String sha) throws IOException{
//		SHA1Digest sha1Digest = new SHA1Digest();
//		sha1Digest.update(sha.getBytes(), 0, sha.length());
//		
//		return encode(sha1Digest.getDigest());
//	}
//	
//    static private final char hex[] = {
//        '0', '1', '2', '3', '4', '5', '6', '7',
//        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
//    };
//
//    public static String encode(byte[] dataStr) throws IOException {
//    	String w = "";
//        for (int i = 0; i < dataStr.length; i++) {
//            int b = dataStr[i];
//            w = w + hex[((b >> 4) & 0xF)] ;
//            w = w + (hex[((b >> 0) & 0xF)]);
//        }
//    	return w;
//    }
//	


//	public static byte[] longToHex(final long l) {
//		long v = l & 0xFFFFFFFFFFFFFFFFL;
//
//		byte[] result = new byte[16];
//		//Arrays.fill(result, 0, result.length, (byte)0);
//
//		for (int i = 0; i < result.length; i += 2) {
//			byte b = (byte) ((v & 0xFF00000000000000L) >> 56);
//
//			byte b2 = (byte) (b & 0x0F);
//			byte b1 = (byte) ((b >> 4) & 0x0F);
//
//			if (b1 > 9) b1 += 39;
//			b1 += 48;
//
//			if (b2 > 9) b2 += 39;
//			b2 += 48;
//
//			result[i] = (byte) (b1 & 0xFF);
//			result[i + 1] = (byte) (b2 & 0xFF);
//
//			v <<= 8;
//		}
//
//		return result;
//	}
}