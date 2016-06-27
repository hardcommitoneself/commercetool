package com.commercetools.sunrise.myaccount.addressbook;

import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.models.AddressBuilder;
import io.sphere.sdk.models.Base;
import play.data.validation.Constraints;

import javax.annotation.Nullable;

public class DefaultAddressFormData extends Base implements AddressFormData {

    private String csrfToken;

    private String title;
    @Constraints.Required
    private String firstName;
    @Constraints.Required
    private String lastName;
    @Constraints.Required
    private String streetName;
    private String additionalStreetInfo;
    @Constraints.Required
    private String city;
    @Constraints.Required
    private String postalCode;
    @Constraints.Required
    private String country;
    private String region;
    private String phone;
    @Constraints.Required
    private String email;

    private boolean defaultShippingAddress;
    private boolean defaultBillingAddress;

    public DefaultAddressFormData() {
    }

    public String validate() {
        final CountryCode country = CountryCode.getByCode(this.country);
        if (country == null || country.equals(CountryCode.UNDEFINED)) {
            return "Invalid country"; // TODO use i18n version
        }
        return null;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(final String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(final String streetName) {
        this.streetName = streetName;
    }

    public String getAdditionalStreetInfo() {
        return additionalStreetInfo;
    }

    public void setAdditionalStreetInfo(final String additionalStreetInfo) {
        this.additionalStreetInfo = additionalStreetInfo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(final String region) {
        this.region = region;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    @Override
    public boolean isDefaultShippingAddress() {
        return defaultShippingAddress;
    }

    public void setDefaultShippingAddress(final boolean defaultShippingAddress) {
        this.defaultShippingAddress = defaultShippingAddress;
    }

    @Override
    public boolean isDefaultBillingAddress() {
        return defaultBillingAddress;
    }

    public void setDefaultBillingAddress(final boolean defaultBillingAddress) {
        this.defaultBillingAddress = defaultBillingAddress;
    }

    @Override
    public void apply(@Nullable final Address address) {
        if (address != null) {
            this.title = address.getTitle();
            this.firstName = address.getFirstName();
            this.lastName = address.getLastName();
            this.streetName = address.getStreetName();
            this.additionalStreetInfo = address.getAdditionalStreetInfo();
            this.city = address.getCity();
            this.postalCode = address.getPostalCode();
            this.country = address.getCountry().getAlpha2();
            this.region = address.getRegion();
            this.phone = address.getPhone();
            this.email = address.getEmail();
        }
    }

    @Override
    public Address extractAddress() {
        final CountryCode country = CountryCode.getByCode(getCountry());
        return AddressBuilder.of(country)
                .title(getTitle())
                .firstName(getFirstName())
                .lastName(getLastName())
                .streetName(getStreetName())
                .additionalStreetInfo(getAdditionalStreetInfo())
                .city(getCity())
                .postalCode(getPostalCode())
                .region(getRegion())
                .phone(getPhone())
                .email(getEmail())
                .build();
    }
}
