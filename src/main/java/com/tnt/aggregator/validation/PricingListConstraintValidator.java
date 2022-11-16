package com.tnt.aggregator.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.regex.Pattern;

public class PricingListConstraintValidator implements ConstraintValidator<PricingListConstraint, List<String>> {

    public static final String ISO2_REGEX = "^A[^ABCHJKNPVY]|B[^CKPUX]|C[^BEJPQST]|D[EJKMOZ]|E[CEGHRST]|F[IJKMOR]|G[^CJKOVXZ]|H[KMNRTU]|I[DEL-OQ-T]|J[EMOP]|K[EGHIMNPRWYZ]|L[ABCIKR-VY]|M[^BIJ]|N[ACEFGILOPRUZ]|OM|P[AE-HK-NRSTWY]|QA|R[EOSUW]|S[^FPQUW]|T[^ABEIPQSUXY]|U[AGMSYZ]|V[ACEGINU]|WF|WS|YE|YT|Z[AMW]$";

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (value.isEmpty()) {
            return false;
        } else {
            if (value.stream().filter(s -> !Pattern.matches(ISO2_REGEX, s)).findAny().isPresent()) {
                return false;
            }
        }
        return true;
    }
}
