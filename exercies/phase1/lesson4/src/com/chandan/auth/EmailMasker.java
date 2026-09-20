package com.chandan.auth;

public class EmailMasker {
    public static void main(String[] args) {
        String emailArg = args.length > 0 ? args[0] : "chandanmarghade@gamil.com";

        StringBuilder email = new StringBuilder(emailArg.strip().toLowerCase());

        //Normalized
        String normalizedEmail = email.toString();
        System.out.println("Normalized > " + normalizedEmail);
        
        // getDomain
        
        int idx = email.indexOf("@");

        if(idx > -1){
            String domain = email.substring(idx);
            System.out.println("Domain > " + domain);
            
            //Masked
            
            String toBeMaskStr = email.substring(1, idx);
            System.out.println(toBeMaskStr);
            String maskedEmail = email.replace(1, idx, "*".repeat(toBeMaskStr.length())).toString();
            System.out.println(maskedEmail);
            
        }else{
            System.out.println("No Domain Found !");
        }


    }
}
