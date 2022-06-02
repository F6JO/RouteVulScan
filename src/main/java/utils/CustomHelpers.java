 package utils;

 public class CustomHelpers {
//   public static int getSecondTimestamp(Date date) {
//     if (date == null)
//       return 0;
//     return Integer.valueOf(String.valueOf(date.getTime() / 1000L)).intValue();
//   }
//
//   public static String randomStr(int number) {
//     StringBuffer s = new StringBuffer();
//     char[] stringArray = {
//         'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
//         'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
//         'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
//         '4', '5', '6', '7', '8', '9' };
//     Random random = new Random();
//     for (int i = 0; i < number; i++)
//       s.append(stringArray[random.nextInt(stringArray.length)]);
//     return s.toString();
//   }
   
   public static boolean isJson(String str) {
     if (str == null || str.isEmpty())
       return false; 
     String str2 = str.trim();
     if (str2.startsWith("{") && str2.endsWith("}"))
       return true; 
     if (!str2.startsWith("[") || !str2.endsWith("]"))
       return false; 
     return true;
   }
 }


