package example.shared;

import com.rabbitmq.client.BuiltinExchangeType;

public class Constant {
    public static class ExchangeConfig {
        public static final String NAME = "logs_routing";
        public static final BuiltinExchangeType TYPE = BuiltinExchangeType.DIRECT;
    }

    public enum Severity {
        INFO("info"),
        WARNING("warning"),
        ERROR("error");

        private final String level;

        Severity(String level) {
            this.level = level;
        }

        public String getLevel() {
            return level;
        }

        public static Severity getSeveritiesFromString(String value) {
            return Severity.valueOf(value.toUpperCase());
        }
    }
}
