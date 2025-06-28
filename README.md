# PawPal Android

PawPal is an Android application designed to help pet owners manage and organize their pet's daily activities, health records, expenses, and important contacts such as veterinarians. With integrated Firebase support for authentication and data storage, PawPal aims to be your all-in-one companion for responsible pet care.

## Features

- **Pet Profile Management:** Create and manage detailed profiles for your pets, including their name, species, gender, birthdate, weight, habitat, and sterilization status.
- **Task Scheduling & Reminders:** Add, update, and delete tasks for your pets (such as feeding, walks, vet visits) with date and time. Set customizable reminders using Android's notification system to never miss an important activity.
- **Expense Tracking:** Log pet-related expenses, keeping track of costs such as food, medical care, and accessories.
- **Veterinarian Contacts:** Search and store veterinary contacts and other important phone numbers, with easy access to call or view details.
- **Firebase Integration:** Secure authentication with Firebase Auth and seamless real-time data storage and retrieval using Firebase Database.
- **Modern Android Development:** Built using AndroidX, Material Design, and follows modern best practices.

## Getting Started

### Prerequisites

- Android Studio (latest recommended)
- Android device or emulator (minimum SDK 24)
- Firebase project set up (for Auth & Realtime Database)

### Installation

1. **Clone the Repository**
   ```sh
   git clone https://github.com/Adi-Sin101/PawPal_Android.git
   cd PawPal_Android
   ```

2. **Open in Android Studio**
   - Open the project folder in Android Studio.

3. **Connect Firebase**
   - Add your `google-services.json` to the `/app` directory.
   - Ensure Firebase Authentication and Realtime Database are enabled in your Firebase console.

4. **Build & Run**
   - Sync Gradle and run the app on your device or emulator.

## Code Structure

- `app/src/main/java/com/example/pawpal_18nov/`
  - `MainActivity.java`: Handles user authentication (sign in, password reset).
  - `Home_Page.java`: Main navigation and fragment management.
  - `ActicityFragment.java`: Core logic for managing pet tasks and reminders.
  - `Make_Profile.java`: UI and logic for creating/editing pet profiles.
  - `VetInfoFragment.java`: Loads and manages veterinary contact information.
  - `Expense.java`, `ContactModel.java`: Data models for expenses and contacts.
  - `ReminderReceiver.java`: Handles scheduled notifications for pet tasks.

## Dependencies

- [Firebase Auth & Database](https://firebase.google.com/)
- AndroidX Libraries (AppCompat, Material, ConstraintLayout)
- Volley (for network requests)
- JUnit (testing)

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for review.

## License

This project is licensed under the MIT License.

---

*PawPal - Your trusted partner in pet care!*
