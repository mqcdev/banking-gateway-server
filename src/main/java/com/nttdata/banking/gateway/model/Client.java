package com.nttdata.banking.gateway.model;

import com.nttdata.banking.gateway.enums.TokenClaims;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

/**
 * Class Client Model.
 * Client microservice class Client.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Client {

    private String idClient;

    @NotEmpty(message = "no debe estar vacío")
    private String names;

    @NotEmpty(message = "no debe estar vacío")
    private String surnames;

    @NotEmpty(message = "no debe estar vacío")
    private String clientType;

    @NotEmpty(message = "no debe estar vacío")
    private String documentType;

    @NotEmpty(message = "no debe estar vacío")
    private String documentNumber;

    @Max(value = 999999999, message = "no debe tener más de 9 cifras")
    private Integer cellphone;

    @Email(message = "debe tener formato de correo")
    private String email;

    @NotEmpty(message = "no debe estar vacío")
    private String userType;

    @NotEmpty(message = "no debe estar vacío")
    private String password;

    private Boolean state;

    private String profile;

    public Map<String, Object> getClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(TokenClaims.ID_CLIENT.name(), this.getIdClient());
        claims.put(TokenClaims.NAMES.name(), this.getNames());
        claims.put(TokenClaims.SURNAMES.name(), this.getSurnames());
        claims.put(TokenClaims.CLIENT_TYPE.name(), this.getClientType());
        claims.put(TokenClaims.DOCUMENT_TYPE.name(), this.getDocumentType());
        claims.put(TokenClaims.DOCUMENT_NUMBER.name(), this.getDocumentNumber());
        claims.put(TokenClaims.CELLPHONE.name(), this.getCellphone());
        claims.put(TokenClaims.EMAIL.name(), this.getEmail());
        claims.put(TokenClaims.STATE.name(), this.getState());
        claims.put(TokenClaims.PROFILE.name(), this.getProfile());
        return claims;
    }

}