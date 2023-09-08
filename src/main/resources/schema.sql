# DROP TABLE IF EXISTS AssociationValueEntry;
# DROP TABLE IF EXISTS DomainEventEntry;
# DROP TABLE IF EXISTS SagaEntry;
# DROP TABLE IF EXISTS SnapshotEventEntry;
# DROP TABLE IF EXISTS TokenEntry;

/** AXON START **/

create table if not exists AssociationValueEntry
(
    id               int auto_increment primary key,
    associationKey   varchar(255) null,
    associationValue varchar(255) null,
    sagaId           varchar(255) null,
    sagaType         varchar(255) null
);

create table if not exists DomainEventEntry
(
    globalIndex         bigint auto_increment primary key,
    aggregateIdentifier varchar(255) not null,
    sequenceNumber      bigint       not null,
    type                varchar(255) null,
    eventIdentifier     varchar(255) not null,
    metaData            blob         null,
    payload             blob         not null,
    payloadRevision     varchar(255) null,
    payloadType         varchar(255) not null,
    timeStamp           varchar(255) not null,
    constraint aggregateIdentifier
        unique (aggregateIdentifier, sequenceNumber),
    constraint eventIdentifier
        unique (eventIdentifier)
);

create table if not exists SagaEntry
(
    sagaId         varchar(255) not null
        primary key,
    revision       varchar(255) null,
    sagaType       varchar(255) null,
    serializedSaga blob         null
);

create table if not exists SnapshotEventEntry
(
    aggregateIdentifier varchar(255) not null,
    sequenceNumber      bigint       not null,
    type                varchar(255) not null,
    eventIdentifier     varchar(255) not null,
    metaData            blob         null,
    payload             blob         not null,
    payloadRevision     varchar(255) null,
    payloadType         varchar(255) not null,
    timeStamp           varchar(255) not null,
    primary key (aggregateIdentifier, sequenceNumber),
    constraint eventIdentifier2 unique (eventIdentifier)
);

create table if not exists TokenEntry
(
    processorName varchar(255) not null,
    segment       int          not null,
    token         blob         null,
    tokenType     varchar(255) null,
    timestamp     varchar(255) null,
    owner         varchar(255) null,
    primary key (processorName, segment)
);

/** AXON END **/


create table if not exists Product
(
    productId    varchar(32)   not null primary key,
    name         varchar(255)  not null,
    amount       decimal       not null,
    currency     varchar(3)    not null,
    cartId       varchar(32)   not null
);