package com.zoop.checkout.app;

/**
 * Created by mainente on 01/07/16.
 */
import java.util.regex.Pattern;

public enum CardType {

    UNKNOWN,
    VISA("^4[0-9]{12}(?:[0-9]{3})?$"),
    MASTERCARD("^5[1-5][0-9]{14}$"),
    AMERICAN_EXPRESS("^3[47][0-9]{13}$"),
    DINERS_CLUB("^3(?:0[0-5]|[68][0-9])[0-9]{11}$"),
    ELO("^((((636368)|(438935)|(504175)|(451416)|(636297))\\d{0,10})|((5067)|(4576)|(4011))\\d{0,12})$"),
    DISCOVER("^6(?:011|5[0-9]{2})[0-9]{12}$"),
    JCB("^(?:2131|1800|35\\d{3})\\d{11}$"),
    ELECTRON("^(4026|417500|4405|4508|4844|4913|4917)d+$"),
    MAESTRO("^(5018|5020|5038|5612|5893|6304|6759|6761|6762|6763|0604|6390)\\d+$"),
    DANKORT("^(5019)\\d+$"),
    AURA("^(5078\\d{2})(\\d{2})(\\d{11})$"),
    INTERPAYMENT("^(636)\\d+$"),
    UNIONPAY("^(62|88)\\d+$");
    private Pattern pattern;

    CardType() {
        this.pattern = null;
    }

    CardType(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public static CardType detect(String cardNumber) {

        for (CardType cardType : CardType.values()) {
            if (null == cardType.pattern) continue;
            if (cardType.pattern.matcher(cardNumber).matches())
                return cardType;
        }

        return UNKNOWN;
    }

}