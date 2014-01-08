/*
 * Copyright (c) 2014 The APN-PROXY Project
 *
 * The APN-PROXY Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.xx_dev.apn.proxy;

import com.xx_dev.apn.proxy.config.ApnProxyConfig;
import com.xx_dev.apn.proxy.config.ApnProxyLocalIpRule;
import org.apache.commons.lang.StringUtils;

/**
 * @author xmx
 * @version $Id: com.xx_dev.apn.proxy.ApnProxyLocalAddressChooser 14-1-8 16:13 (xmx) Exp $
 */
public class ApnProxyLocalAddressChooser {

    public static String choose(String hostName) {

        for (ApnProxyLocalIpRule localIpRule : ApnProxyConfig.getConfig().getLocalIpRuleList()) {
            for (String originalHost : localIpRule.getOriginalHostList()) {
                if (StringUtils.equals(originalHost, hostName)
                        || StringUtils.endsWith(hostName, "." + originalHost)) {
                    return localIpRule.getLocalIp();
                }
            }
        }

        return null;
    }

}
