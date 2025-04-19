-- 上游系统表
create table UpstreamSystems (
    id int not null auto_increment,
    name varchar(255) not null unique,
    description varchar(255),
    primary key (id)
);

-- 下游系统表
create table DownstreamSystems (
    id int not null auto_increment,
    name varchar(255) not null unique,
    description varchar(255),
    primary key (id)
);

-- SFTP 连接详情表
create table ConnectionDetails (
    id int not null auto_increment,
    host varchar(255) not null,
    port int not null,
    username varchar(255) not null,
    password varchar(255) not null,
    primary key (id)
);

-- 文件传输记录表
create table FileTransfers (
    id int not null auto_increment,
    file_name varchar(255) not null,
    file_size bigint not null,
    transfer_status varchar(50) not null, -- e.g., PENDING, IN_PROGRESS, COMPLETED, FAILED
    transfer_date timestamp default current_timestamp,
    upstream_system_id int not null,
    downstream_system_id int not null,
    connection_detail_id int not null,
    error_message varchar(255),
    primary key (id),
    foreign key (upstream_system_id) references UpstreamSystems(id),
    foreign key (downstream_system_id) references DownstreamSystems(id),
    foreign key (connection_detail_id) references ConnectionDetails(id)
);