package com.xx_dev.apn.proxy.config;

import com.xx_dev.apn.proxy.ApnProxyConfigException;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParsingException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mingxing.xumx
 * Date: 13-10-15
 * Time: 下午8:11
 * To change this template use File | Settings | File Templates.
 */
public class ApnProxyXmlConfigReader {

    private static final Logger logger = Logger.getLogger(ApnProxyXmlConfigReader.class);

    public static void read(InputStream xmlConfigFileInputStream) {
        Document doc = null;
        try {
            Builder parser = new Builder();
            doc = parser.build(xmlConfigFileInputStream);
        } catch (ParsingException ex) {
            logger.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
        if (doc == null) {
            return;
        }
        Element rootElement = doc.getRootElement();

        Elements listenTypeElements = rootElement.getChildElements("listen-type");
        if (listenTypeElements.size() == 1) {
            String _listenType = listenTypeElements.get(0).getValue();
            ApnProxyConfig.getConfig().setListenType(ApnProxyListenType.fromString(_listenType));
        }

        Elements tripleDesKeyElements = rootElement.getChildElements("triple-des-key");
        if (tripleDesKeyElements.size() == 1) {
            ApnProxyConfig.getConfig().setTripleDesKey(tripleDesKeyElements.get(0).getValue());
        }

        Elements keyStoreElements = rootElement.getChildElements("key-store");
        if (keyStoreElements.size() == 1) {
            Elements keyStorePathElements = keyStoreElements.get(0).getChildElements("path");
            if (keyStorePathElements.size() == 1) {
                ApnProxyConfig.getConfig().setKeyStorePath(keyStorePathElements.get(0).getValue());
            }
            Elements keyStorePasswordElements = keyStoreElements.get(0)
                    .getChildElements("password");
            if (keyStorePasswordElements.size() == 1) {
                ApnProxyConfig.getConfig().setKeyStroePassword(keyStorePasswordElements.get(0).getValue());
            }
        }

        Elements trustStoreElements = rootElement.getChildElements("trust-store");
        if (trustStoreElements.size() == 1) {
            ApnProxyConfig.getConfig().setUseTrustStore(true);
            Elements trustStorePathElements = trustStoreElements.get(0).getChildElements("path");
            if (trustStorePathElements.size() == 1) {
                ApnProxyConfig.getConfig().setTrustStorePath(trustStorePathElements.get(0).getValue());
            }
            Elements trustStorePasswordElements = trustStoreElements.get(0)
                    .getChildElements("password");
            if (trustStorePasswordElements.size() == 1) {
                ApnProxyConfig.getConfig().setTrustStorePassword(trustStorePasswordElements.get(0).getValue());
            }
        }

        Elements portElements = rootElement.getChildElements("port");
        if (portElements.size() == 1) {
            try {
                ApnProxyConfig.getConfig().setPort(Integer.parseInt(portElements.get(0).getValue()));
            } catch (NumberFormatException nfe) {
                throw new ApnProxyConfigException("Invalid format for: port", nfe);
            }
        }

        Elements threadCountElements = rootElement.getChildElements("thread-count");
        if (threadCountElements.size() == 1) {
            Elements bossElements = threadCountElements.get(0).getChildElements("boss");
            if (bossElements.size() == 1) {
                try {
                    ApnProxyConfig.getConfig().setBossThreadCount(Integer.parseInt(bossElements.get(0).getValue()));
                } catch (NumberFormatException nfe) {
                    throw new ApnProxyConfigException("Invalid format for: boss", nfe);
                }
            }
            Elements workerElements = threadCountElements.get(0).getChildElements("worker");
            if (workerElements.size() == 1) {
                try {
                    ApnProxyConfig.getConfig().setWorkerThreadCount(Integer.parseInt(workerElements.get(0).getValue()));
                } catch (NumberFormatException nfe) {
                    throw new ApnProxyConfigException("Invalid format for: worker", nfe);
                }
            }
        }

        Elements pacHostElements = rootElement.getChildElements("pac-host");
        if (pacHostElements.size() == 1) {
            ApnProxyConfig.getConfig().setPacHost(pacHostElements.get(0).getValue());
        }

        Elements useIpv6Elements = rootElement.getChildElements("use-ipv6");
        if (useIpv6Elements.size() == 1) {
            ApnProxyConfig.getConfig().setUseIpV6(Boolean.parseBoolean(useIpv6Elements.get(0).getValue()));
        }

        Elements remoteRulesElements = rootElement.getChildElements("remote-rules");
        if (remoteRulesElements.size() == 1) {
            Elements ruleElements = remoteRulesElements.get(0).getChildElements("rule");

            for (int i = 0; i < ruleElements.size(); i++) {
                ApnProxyRemoteRule apnProxyRemoteRule = new ApnProxyRemoteRule();

                Element ruleElement = ruleElements.get(i);

                Elements remoteHostElements = ruleElement.getChildElements("remote-host");
                if (remoteHostElements.size() != 1) {
                    throw new ApnProxyConfigException("Wrong config for: remote-host");
                }
                String remoteHost = remoteHostElements.get(0).getValue();

                apnProxyRemoteRule.setRemoteHost(remoteHost);

                Elements remotePortElements = ruleElement.getChildElements("remote-port");
                if (remoteHostElements.size() != 1) {
                    throw new ApnProxyConfigException("Wrong config for: remote-port");
                }
                int remotePort = -1;
                try {
                    remotePort = Integer.parseInt(remotePortElements.get(0).getValue());
                } catch (NumberFormatException nfe) {
                    throw new ApnProxyConfigException("Invalid format for: remote-port", nfe);
                }

                apnProxyRemoteRule.setRemotePort(remotePort);

                Elements proxyUserNameElements = ruleElement.getChildElements("proxy-username");
                if (proxyUserNameElements.size() == 1) {
                    String proxyUserName = proxyUserNameElements.get(0).getValue();
                    apnProxyRemoteRule.setProxyUserName(proxyUserName);
                }


                Elements proxyPasswordElements = ruleElement.getChildElements("proxy-password");
                if (proxyPasswordElements.size() == 1) {
                    String proxyPassword = proxyPasswordElements.get(0).getValue();
                    apnProxyRemoteRule.setProxyPassword(proxyPassword);
                }

                Elements remoteListenTypeElements = ruleElement
                        .getChildElements("remote-listen-type");
                if (remoteListenTypeElements.size() != 1) {
                    throw new ApnProxyConfigException("Wrong config for: remote-listen-type");
                }
                String _remoteListenType = remoteListenTypeElements.get(0).getValue();
                ApnProxyListenType remoteListenType = ApnProxyListenType
                        .fromString(_remoteListenType);
                apnProxyRemoteRule.setRemoteListenType(remoteListenType);

                if (remoteListenType == ApnProxyListenType.TRIPLE_DES) {
                    Elements remoteTripleDesKeyElements = ruleElement
                            .getChildElements("remote-3des-key");
                    if (remoteListenTypeElements.size() > 1) {
                        throw new ApnProxyConfigException("Wrong config for: remote-3des-key");
                    }
                    String remoteTripleDesKey = remoteTripleDesKeyElements.get(0).getValue();
                    apnProxyRemoteRule.setRemoteTripleDesKey(remoteTripleDesKey);
                }

                if (remoteListenType == ApnProxyListenType.SSL) {
                    //ApnProxySSLContextFactory.createSSLContext(remoteHost, remotePort);
                }

                // simple key; ssl trust store

                Elements applyListElements = ruleElement.getChildElements("apply-list");
                if (applyListElements.size() == 1) {
                    Elements originalHostElements = applyListElements.get(0).getChildElements(
                            "original-host");

                    List<String> originalHostList = new ArrayList<String>();
                    for (int j = 0; j < originalHostElements.size(); j++) {
                        String originalHost = originalHostElements.get(j).getValue();
                        originalHostList.add(originalHost);
                    }
                    apnProxyRemoteRule.setOriginalHostList(originalHostList);
                }

                ApnProxyConfig.getConfig().addRemoteRule(apnProxyRemoteRule);
            }
        }

        Elements localIpRulesElements = rootElement.getChildElements("local-ip-rules");
        if (localIpRulesElements.size() == 1) {
            Elements ruleElements = localIpRulesElements.get(0).getChildElements("rule");

            for (int i = 0; i < ruleElements.size(); i++) {
                ApnProxyLocalIpRule apnProxyLocalIpRule = new ApnProxyLocalIpRule();

                Element ruleElement = ruleElements.get(i);

                Elements localIpElements = ruleElement.getChildElements("local-ip");
                if (localIpElements.size() != 1) {
                    throw new ApnProxyConfigException("Wrong config for: local-ip");
                }
                String localIp = localIpElements.get(0).getValue();

                apnProxyLocalIpRule.setLocalIp(localIp);

                Elements applyListElements = ruleElement.getChildElements("apply-list");
                if (applyListElements.size() == 1) {
                    Elements originalHostElements = applyListElements.get(0).getChildElements(
                            "original-host");

                    List<String> originalHostList = new ArrayList<String>();
                    for (int j = 0; j < originalHostElements.size(); j++) {
                        String originalHost = originalHostElements.get(j).getValue();
                        originalHostList.add(originalHost);
                    }
                    apnProxyLocalIpRule.setOriginalHostList(originalHostList);
                }

                ApnProxyConfig.getConfig().addLocalIpRuleList(apnProxyLocalIpRule);
            }

        }

    }

    public static void read(File xmlConfigFile) throws FileNotFoundException {
        if (xmlConfigFile.exists() && xmlConfigFile.isFile()) {
            read(new FileInputStream(xmlConfigFile));
        }
    }

}