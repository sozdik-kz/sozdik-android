package kz.sozdik.login.data.api.model;

import kz.sozdik.core.models.CommonRequest;

public class SocialAuthOutput extends CommonRequest {

    public static final String FACEBOOK = "facebook";
    public static final String GOOGLE = "google";

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String gender;
    private String socialNetwork;
    private String socialNetworkId;
    private String birthDate;
    private String avatarUrl;

    private SocialAuthOutput(Builder builder) {
        super(builder);
        email = builder.email;
        password = builder.password;
        firstName = builder.firstName;
        lastName = builder.lastName;
        gender = builder.gender;
        socialNetwork = builder.socialNetwork;
        socialNetworkId = builder.socialNetworkId;
        birthDate = builder.birthDate;
        avatarUrl = builder.avatarUrl;
    }

    public static final class Builder extends CommonRequest.Builder<Builder> {

        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String gender;
        private String socialNetwork;
        private String socialNetworkId;
        private String birthDate;
        private String avatarUrl;

        public Builder() {
        }

        public Builder email(String val) {
            email = val;
            return this;
        }

        public Builder password(String val) {
            password = val;
            return this;
        }

        public Builder firstName(String val) {
            firstName = val;
            return this;
        }

        public Builder lastName(String val) {
            lastName = val;
            return this;
        }

        public Builder gender(String val) {
            gender = val;
            return this;
        }

        public Builder socialNetwork(String val) {
            socialNetwork = val;
            return this;
        }

        public Builder socialNetworkId(String val) {
            socialNetworkId = val;
            return this;
        }

        public Builder birthDate(String val) {
            birthDate = val;
            return this;
        }

        public Builder avatarUrl(String val) {
            avatarUrl = val;
            return this;
        }

        public SocialAuthOutput build() {
            return new SocialAuthOutput(this);
        }
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public String getSocialNetworkId() {
        return socialNetworkId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
