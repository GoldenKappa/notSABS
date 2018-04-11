package com.layoutxml.sabs.blocker;

import android.support.annotation.Nullable;
import android.util.Log;

import com.layoutxml.sabs.App;
import com.sec.enterprise.firewall.DomainFilterRule;
import com.sec.enterprise.firewall.Firewall;
import com.sec.enterprise.firewall.FirewallResponse;
import com.sec.enterprise.firewall.FirewallRule;

import java.util.List;

import javax.inject.Inject;


public class fwInterface {

    private final String TAG = fwInterface.class.getCanonicalName();

    @Nullable
    @Inject
    Firewall mFirewall;

    public fwInterface() {
        App.get().getAppComponent().inject(this);
    }

    public boolean addDomainFilterRules(List<DomainFilterRule> dfRules)
    {
        // If there are domainfilter rules to process
        if(!dfRules.isEmpty()) {

            try
            {
                // Add rules to the firewall
                FirewallResponse[] response = mFirewall.addDomainFilterRules(dfRules);

                // If the domains are added successfully
                if (FirewallResponse.Result.SUCCESS == response[0].getResult()) {
                    // Output to debug
                    Log.i(TAG, "Domain Filter rule(s) added successfully.");
                } else {
                    // Output to debug
                    Log.e(TAG, response[0].getResult().toString());
                    // Return enabling failed
                    return false;
                }
            }
            catch(SecurityException ex)
            {
                Log.e(TAG, ex.toString());
                return false;
            }
        }

        return true;
    }

    public boolean removeDomainFilterRules(List<DomainFilterRule> dfRules)
    {
        // If there are domainfilter rules to process
        if(!dfRules.isEmpty()) {

            try
            {
                // Add rules to the firewall
                FirewallResponse[] response = mFirewall.removeDomainFilterRules(dfRules);

                // If the domains are added successfully
                if (FirewallResponse.Result.SUCCESS == response[0].getResult()) {
                    // Output to debug
                    Log.i(TAG, "Domain Filter rule(s) removed successfully.");
                } else {
                    // Output to debug
                    Log.e(TAG, response[0].getResult().toString());
                    // Return enabling failed
                    return false;
                }
            }
            catch(SecurityException ex)
            {
                Log.e(TAG, ex.toString());
                return false;
            }
        }

        return true;
    }


    public boolean addFirewallRules(FirewallRule[] fwRules)
    {
        // If there are firewall rules to process
        if(fwRules.length > 0) {

            try
            {
                // Add rules to the firewall
                FirewallResponse[] response = mFirewall.addRules(fwRules);

                if (FirewallResponse.Result.SUCCESS == response[0].getResult()) {
                    Log.i(TAG, "Firewall rule(s) added successfully.");
                }
                else
                {
                    // Output to debug
                    Log.d(TAG, "Firewall rule(s) skipped.");
                }

            } catch (SecurityException ex) {
                Log.e(TAG, ex.toString());
                return false;
            }
        }

        return true;
    }

    public boolean isEnabled() {
        return mFirewall.isFirewallEnabled();
    }

}
