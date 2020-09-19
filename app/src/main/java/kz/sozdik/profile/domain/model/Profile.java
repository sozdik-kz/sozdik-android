package kz.sozdik.profile.domain.model;

import androidx.room.Entity;
import android.text.TextUtils;

import java.util.List;

@Entity(tableName = "profiles",
        primaryKeys = {"id"})
public class Profile {

    private int id;

    private String userName;

    private String firstName;

    private String lastName;

    private String middleName;

    private String gender;

    private String dob;

    private String email;

    private int accountTime;

    private int accountType;

    private int active;

    private String avatarUrl;

    private int cardConfirmed;

    private int cardNumber;

    private int cityId;

    private String country;

    private int emailNotifications;

    private int groupId;

    private int history;

    private int likes;

    private int loginTime;

    private int logoutAllTime;

    private int passwordSet;

    private int phone;

    private int phoneConfirmed;

    private int pushNotifications;

    private int rating;

    private int registrationTime;

    private int smsNotifications;

    private List<String> socialNetworks = null;

    private int subscription;

    private int timezone;

    public Profile() {
    }

    public int getAccountTime() {
        return accountTime;
    }

    public void setAccountTime(int accountTime) {
        this.accountTime = accountTime;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public int getCardConfirmed() {
        return cardConfirmed;
    }

    public void setCardConfirmed(int cardConfirmed) {
        this.cardConfirmed = cardConfirmed;
    }

    public int getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(int cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(int emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(firstName)) {
            sb.append(firstName);
            sb.append(" ");
        }
        if (!TextUtils.isEmpty(lastName)) {
            sb.append(lastName);
        }
        return sb.toString();
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(int loginTime) {
        this.loginTime = loginTime;
    }

    public int getLogoutAllTime() {
        return logoutAllTime;
    }

    public void setLogoutAllTime(int logoutAllTime) {
        this.logoutAllTime = logoutAllTime;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public int getPasswordSet() {
        return passwordSet;
    }

    public void setPasswordSet(int passwordSet) {
        this.passwordSet = passwordSet;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public int getPhoneConfirmed() {
        return phoneConfirmed;
    }

    public void setPhoneConfirmed(int phoneConfirmed) {
        this.phoneConfirmed = phoneConfirmed;
    }

    public int getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(int pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(int registrationTime) {
        this.registrationTime = registrationTime;
    }

    public int getSmsNotifications() {
        return smsNotifications;
    }

    public void setSmsNotifications(int smsNotifications) {
        this.smsNotifications = smsNotifications;
    }

    public List<String> getSocialNetworks() {
        return socialNetworks;
    }

    public void setSocialNetworks(List<String> socialNetworks) {
        this.socialNetworks = socialNetworks;
    }

    public int getSubscription() {
        return subscription;
    }

    public void setSubscription(int subscription) {
        this.subscription = subscription;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}