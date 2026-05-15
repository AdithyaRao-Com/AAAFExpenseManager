# AAAF V2 - Personal Expense Manager

AAAF V2 is a comprehensive Android application designed to help users manage their personal finances efficiently. It provides a robust set of tools for tracking transactions, managing accounts, scheduling recurring payments, and visualizing financial data through detailed reports.

## Features

### 💰 Transaction Management
*   **Track Everything:** Easily log Income, Expenses, and Transfers between accounts.
*   **Recurring Transactions:** Automate your fixed expenses or regular income with flexible scheduling (daily, weekly, monthly, etc.).
*   **Copy Transactions:** Quickly duplicate previous entries to save time.
*   **Advanced Filtering:** Search and filter transactions by date, category, account, amount, and more.

### 🏦 Account & Category Management
*   **Multiple Accounts:** Manage various accounts like Cash, Savings, Credit Cards, etc.
*   **Account Types & Tags:** Group and categorize accounts for better organization.
*   **Hierarchy:** Support for parent and sub-categories to track spending in detail.
*   **Balance Tracking:** Real-time balance updates for all accounts.

### 📊 Reports & Insights
*   **Forecast Summary:** Visualize your future financial health based on current balances and scheduled recurring transactions.
*   **Category Summary:** Analyze your spending patterns with intuitive charts and breakdowns.
*   **Custom Date Ranges:** Generate reports for specific periods.

### 💾 Data Security & Portability
*   **Secure Backups:** Export and import your entire database as an encrypted ZIP file.
*   **Import/Export:** Support for CSV and QIF formats for easy data migration.
*   **Auto Backup:** Configure automatic backups to a selected directory.

### 🌍 Localization & Customization
*   **Multi-Currency Support:** Manage transactions in different currencies with customizable conversion factors.
*   **Flexible Settings:** Customize account types, categories, and application behavior.

## Tech Stack

*   **Language:** Java
*   **Architecture:** MVVM (Model-View-ViewModel) with Android Architecture Components.
*   **Local Database:** SQLite (Room/OpenHelper)
*   **UI Components:** Material Design 3, Jetpack Navigation, ConstraintLayout.
*   **Libraries:**
    *   **MPAndroidChart:** For data visualization.
    *   **Zip4j:** For secure database compression and encryption.
    *   **Gson:** For JSON serialization.
    *   **Apache Commons CSV:** For CSV handling.

## Getting Started

1.  Clone the repository:
    ```bash
    git clone https://github.com/AdithyaRao-Com/AAAFExpenseManager.git
    ```
2.  Open the project in **Android Studio**.
3.  Build and run the app on an emulator or a physical device.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
