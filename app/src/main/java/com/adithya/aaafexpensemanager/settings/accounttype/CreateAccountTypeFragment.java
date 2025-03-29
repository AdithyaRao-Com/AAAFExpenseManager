package com.adithya.aaafexpensemanager.settings.accounttype;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.adithya.aaafexpensemanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CreateAccountTypeFragment extends Fragment {
    private AccountTypeViewModel accountTypeViewModel;
    private EditText accountTypeNameEditText;
    private EditText accountTypeDisplayOrderEditText;
    /**
     * @noinspection FieldCanBeLocal
     */
    private FloatingActionButton createAccountTypeButton;
    /**
     * @noinspection FieldCanBeLocal
     */
    private AccountType originalAccountType;

    private void setEditTextEnabled(EditText editTextField, boolean enabledFlag) {
        editTextField.setEnabled(enabledFlag);
        editTextField.setFocusable(enabledFlag);
        editTextField.setFocusableInTouchMode(enabledFlag);
    }

    /**
     * @noinspection deprecation, CallToPrintStackTrace
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account_type, container, false);
        accountTypeViewModel = new ViewModelProvider(requireActivity()).get(AccountTypeViewModel.class);
        accountTypeNameEditText = view.findViewById(R.id.accountTypeNameEditText);
        accountTypeDisplayOrderEditText = view.findViewById(R.id.accountTypeDisplayOrderEditText);
        createAccountTypeButton = view.findViewById(R.id.createAccountTypeFab);
        if (getArguments() != null && getArguments().containsKey("accountType")) {
            originalAccountType = getArguments().getParcelable("accountType");
            if (originalAccountType != null) {
                accountTypeNameEditText.setText(originalAccountType.accountType);
                accountTypeDisplayOrderEditText.setText(String.valueOf(originalAccountType.accountTypeDisplayOrder));
                setEditTextEnabled(accountTypeNameEditText, false);
            } else {
                setEditTextEnabled(accountTypeNameEditText, true);
            }
        } else {
            setEditTextEnabled(accountTypeNameEditText, true);
        }
        createAccountTypeButton.setOnClickListener(v -> {
            String accountTypeName;
            try {
                accountTypeName = accountTypeNameEditText.getText().toString().trim();
                if (accountTypeName.isBlank()) throw new Exception();
            } catch (Exception e) {
                accountTypeNameEditText.setError("Account type name cannot be empty");
                e.printStackTrace();
                return;
            }
            int accountTypeDisplayOrder;
            try {
                accountTypeDisplayOrder = Integer.parseInt(accountTypeDisplayOrderEditText.getText().toString());
            } catch (Exception e) {
                accountTypeDisplayOrderEditText.setError("Display order cannot be empty");
                e.printStackTrace();
                return;
            }
            accountTypeViewModel.createAccountType(new AccountType(accountTypeName, accountTypeDisplayOrder));
            Navigation.findNavController(requireView()).navigate(R.id.action_createAccountTypeFragment_to_accountTypeFragment, null);
        });
        return view;
    }
}
