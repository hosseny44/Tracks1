package com.example.tracks;

import android.os.Parcel;

import java.util.ArrayList;

public class User {

        private String firstName;
        private String lastName;

        private String phone;

        private String photo;
        private  String password;
        private String email;

        private ArrayList<String> favorites;

        public User() {
        }

        public User(String firstName, String lastName, String phone, String photo, String password,String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.photo = photo;
            this.favorites = new ArrayList<>();
            this.password= password;
            this.email= email;

        }

        public User(Parcel in) {
        }



        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }


        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public ArrayList<String> getFavorites() {
            return favorites;
        }

        public void setFavorites(ArrayList<String> favourits) {
            this.favorites = favourits;
        }

        @Override
        public String toString() {
            return "User{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", phone='" + phone + '\'' +
                    ", photo='" + photo + '\'' +
                    ", password='" + password + '\'' +
                    ", email='" + email + '\'' +

                    ", favorites=" + favorites +
                    '}';
        }
    }


