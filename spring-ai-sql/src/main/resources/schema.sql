create table Users (
    id int not null auto_increment,
    username varchar(255) not null unique,
    email varchar(255) not null unique,
    created_at timestamp default current_timestamp,
    primary key (id)
);

create table Products (
    id int not null auto_increment,
    name varchar(255) not null,
    price decimal(10, 2) not null,
    stock int not null,
    primary key (id)
);

create table Orders (
    id int not null auto_increment,
    user_ref int not null,
    product_ref int not null,
    quantity int not null,
    order_date timestamp default current_timestamp,
    primary key (id),
    foreign key (user_ref) references Users(id),
    foreign key (product_ref) references Products(id)
);

