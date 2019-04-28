create table orders (id int not null auto_increment, create_date date, modified_date date, price double not null, state varchar(255), user_name varchar(255) not null, primary key (id));
create table reserved_item (id int not null auto_increment, order_id binary not null references orders(id), primary key (id));
