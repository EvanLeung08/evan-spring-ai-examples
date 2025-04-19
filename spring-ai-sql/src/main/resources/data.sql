-- 插入上游系统数据
insert into UpstreamSystems (name, description) values ('SystemA', 'Primary data source');
insert into UpstreamSystems (name, description) values ('SystemB', 'Backup data source');
insert into UpstreamSystems (name, description) values ('SystemC', 'Legacy data source');
insert into UpstreamSystems (name, description) values ('SystemD', 'External API integration');
insert into UpstreamSystems (name, description) values ('SystemE', 'Cloud storage service');

-- 插入下游系统数据
insert into DownstreamSystems (name, description) values ('SystemX', 'Data warehouse');
insert into DownstreamSystems (name, description) values ('SystemY', 'Analytics platform');
insert into DownstreamSystems (name, description) values ('SystemZ', 'Reporting platform');
insert into DownstreamSystems (name, description) values ('SystemW', 'Machine learning pipeline');
insert into DownstreamSystems (name, description) values ('SystemV', 'Real-time monitoring system');

-- 插入连接详情数据
insert into ConnectionDetails (host, port, username, password) values ('sftp.server1.com', 22, 'user1', 'password1');
insert into ConnectionDetails (host, port, username, password) values ('sftp.server2.com', 22, 'user2', 'password2');
insert into ConnectionDetails (host, port, username, password) values ('sftp.server3.com', 22, 'user3', 'password3');
insert into ConnectionDetails (host, port, username, password) values ('sftp.server4.com', 22, 'user4', 'password4');
insert into ConnectionDetails (host, port, username, password) values ('sftp.server5.com', 22, 'user5', 'password5');

-- 插入文件传输记录数据
insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240101.csv', 1024, 'COMPLETED', 1, 1, 1, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240102.csv', 2048, 'FAILED', 1, 2, 2, 'Connection timeout');

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240103.csv', 512, 'IN_PROGRESS', 2, 1, 1, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240104.csv', 4096, 'PENDING', 2, 2, 2, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240105.csv', 1024, 'COMPLETED', 3, 3, 3, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240106.csv', 2048, 'FAILED', 4, 4, 4, 'Authentication failed');

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240107.csv', 512, 'IN_PROGRESS', 5, 5, 5, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240108.csv', 4096, 'PENDING', 1, 3, 2, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240109.csv', 8192, 'COMPLETED', 2, 4, 1, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240110.csv', 16384, 'FAILED', 3, 5, 3, 'File not found');

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240111.csv', 256, 'COMPLETED', 4, 1, 4, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240112.csv', 128, 'PENDING', 5, 2, 5, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240113.csv', 5120, 'IN_PROGRESS', 1, 5, 2, null);

insert into FileTransfers (file_name, file_size, transfer_status, upstream_system_id, downstream_system_id, connection_detail_id, error_message)
values ('data_20240114.csv', 10240, 'FAILED', 2, 3, 1, 'Connection reset');