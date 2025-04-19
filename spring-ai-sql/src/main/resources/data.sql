-- 插入用户数据
insert into Users (username, email) values ('alice', 'alice@example.com');
insert into Users (username, email) values ('bob', 'bob@example.com');
insert into Users (username, email) values ('charlie', 'charlie@example.com');

-- 插入产品数据
insert into Products (name, price, stock) values ('Laptop', 999.99, 10);
insert into Products (name, price, stock) values ('Smartphone', 499.99, 20);
insert into Products (name, price, stock) values ('Headphones', 49.99, 50);

-- 插入订单数据
insert into Orders (user_ref, product_ref, quantity) values (1, 1, 1);
insert into Orders (user_ref, product_ref, quantity) values (2, 2, 2);
insert into Orders (user_ref, product_ref, quantity) values (3, 3, 3);
insert into Orders (user_ref, product_ref, quantity) values (1, 3, 5);