package fr.davidson.diff.jjoules.class_instrumentation.process;

import fr.davidson.diff.jjoules.class_instrumentation.process.junit5.InternalJJoulesProcessor;

public enum JUnitVersion {
    JUNIT3() {
        @Override
        public String getTearDownClassFullQualifiedName() {
            return "java.lang.Override";
        }

        @Override
        public String getInitClassFullQualifiedName() {
            return "java.lang.Override";
        }

        @Override
        public AbstractInternalJJoulesProcessor getInternalProcessor() {
            return new fr.davidson.diff.jjoules.class_instrumentation.process.junit3.InternalJJoulesProcessor(this);
        }
    }, JUNIT4() {
        @Override
        public String getTearDownClassFullQualifiedName() {
            return "org.junit.AfterClass";
        }

        @Override
        public String getInitClassFullQualifiedName() {
            return "org.junit.BeforeClass";
        }
    }, JUNIT5() {
        @Override
        public String getTearDownClassFullQualifiedName() {
            return "org.junit.jupiter.api.AfterAll";
        }

        @Override
        public String getInitClassFullQualifiedName() {
            return "org.junit.jupiter.api.BeforeAll";
        }
    };

    public AbstractInternalJJoulesProcessor getInternalProcessor() {
        return new InternalJJoulesProcessor(this);
    }

    public abstract String getTearDownClassFullQualifiedName();

    public abstract String getInitClassFullQualifiedName();

}