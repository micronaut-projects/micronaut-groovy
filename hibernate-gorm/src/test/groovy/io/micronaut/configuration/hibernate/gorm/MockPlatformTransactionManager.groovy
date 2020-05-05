package io.micronaut.configuration.hibernate.gorm

import org.springframework.lang.Nullable
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionException
import org.springframework.transaction.TransactionStatus
import javax.inject.Singleton

@Singleton
class MockPlatformTransactionManager implements PlatformTransactionManager {
    @Override
    TransactionStatus getTransaction(@Nullable TransactionDefinition definition) throws TransactionException {
        return null
    }

    @Override
    void commit(TransactionStatus status) throws TransactionException {

    }

    @Override
    void rollback(TransactionStatus status) throws TransactionException {

    }
}
