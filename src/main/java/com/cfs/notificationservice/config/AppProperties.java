package com.cfs.notificationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Kafka kafka = new Kafka();
    private final Mail mail = new Mail();


    public Kafka getKafka()
    {
        return kafka;
    }

    public Mail getMail()
    {
        return mail;
    }


    public static class Kafka{

        private String topic;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }

   public static class Mail{

        private boolean enabled;
        private String from;

       public boolean isEnabled() {
           return enabled;
       }

       public void setEnabled(boolean enabled) {
           this.enabled = enabled;
       }

       public String getFrom() {
           return from;
       }

       public void setFrom(String from) {
           this.from = from;
       }
   }
}

