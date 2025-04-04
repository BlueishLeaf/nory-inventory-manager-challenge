CREATE SCHEMA IF NOT EXISTS inventory_management;

CREATE TABLE IF NOT EXISTS inventory_management.location (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL,
    address varchar(255) NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory_management.staff_member (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL,
    date_of_birth timestamp NOT NULL,
    role varchar(255) NOT NULL,
    iban varchar(255) NOT NULL,
    bic varchar(255) NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

-- For handling the Many-to-Many that exists between locations and staff members
CREATE TABLE IF NOT EXISTS inventory_management.staff_member_location (
    staff_member_id bigint NOT NULL REFERENCES inventory_management.staff_member(id),
    location_id bigint NOT NULL REFERENCES inventory_management.location(id),
    PRIMARY KEY (staff_member_id, location_id)
);

CREATE TABLE IF NOT EXISTS inventory_management.ingredient (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL,
    unit varchar(255) NOT NULL,
    cost decimal NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

-- For keeping track of location-specific info related to ingredients, such as the quantity currently in stock
CREATE TABLE IF NOT EXISTS inventory_management.location_ingredient (
    location_id bigint NOT NULL REFERENCES inventory_management.location(id),
    ingredient_id bigint NOT NULL REFERENCES inventory_management.ingredient(id),
    quantity decimal NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY (location_id, ingredient_id)
);

CREATE TABLE IF NOT EXISTS inventory_management.recipe (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

-- For keeping track of recipe-specific info related to ingredients, such as the quantity used in the recipe
CREATE TABLE IF NOT EXISTS inventory_management.recipe_ingredient (
    recipe_id bigint NOT NULL REFERENCES inventory_management.recipe(id),
    ingredient_id bigint NOT NULL REFERENCES inventory_management.ingredient(id),
    quantity decimal NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY (recipe_id, ingredient_id)
);

-- Split modifiers out into modifier_type and modifier tables, easier for lookups
CREATE TABLE IF NOT EXISTS inventory_management.modifier_type (
    id bigint NOT NULL PRIMARY KEY,
    name varchar(255) NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory_management.modifier (
    id bigserial NOT NULL PRIMARY KEY,
    modifier_type_id bigint NOT NULL REFERENCES inventory_management.modifier_type(id),
    option varchar(255) NOT NULL,
    price decimal NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory_management.menu_item (
    recipe_id bigint NOT NULL REFERENCES inventory_management.recipe(id),
    location_id bigint NOT NULL REFERENCES inventory_management.location(id),
    modifier_type_id bigint NULL REFERENCES inventory_management.modifier_type(id),
    price decimal NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL,
    PRIMARY KEY (recipe_id, location_id)
);

-- The main auditing table used for tracking all inventory-related changes in the application
CREATE TABLE IF NOT EXISTS inventory_management.inventory_audit_log (
    id bigserial NOT NULL PRIMARY KEY,
    location_id bigint NOT NULL,
    staff_member_id bigint NOT NULL,
    staff_member_name varchar(255) NOT NULL,
    ingredient_id bigint NOT NULL,
    ingredient_name varchar(255) NOT NULL,
    quantity_change_type varchar(32) NOT NULL,
    quantity_before decimal NOT NULL,
    quantity_after decimal NOT NULL,
    quantity_change_amount decimal NOT NULL,
    quantity_change_cost decimal NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

-- Allows tracking of when deliveries were accepted and by which staff member.
CREATE TABLE IF NOT EXISTS inventory_management.delivery (
    id bigserial NOT NULL PRIMARY KEY,
    location_id bigint NOT NULL,
    staff_member_id bigint NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

-- Links the delivery table to the audit logs, allowing the app to query what ingredients were accepted during a particular delivery
CREATE TABLE IF NOT EXISTS inventory_management.delivery_audit_log (
    delivery_id bigint NOT NULL REFERENCES inventory_management.delivery(id),
    audit_log_id bigint NOT NULL REFERENCES inventory_management.inventory_audit_log(id),
    PRIMARY KEY (delivery_id, audit_log_id)
);

-- Allows tracking of when stocktakes were performed and by which staff member.
CREATE TABLE IF NOT EXISTS inventory_management.stocktake (
    id bigserial NOT NULL PRIMARY KEY,
    location_id bigint NOT NULL,
    staff_member_id bigint NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

-- Links the stocktake table to the audit logs, allowing the app to query what stock corrections were made during a particular stocktake
CREATE TABLE IF NOT EXISTS inventory_management.stocktake_audit_log (
    stocktake_id bigint NOT NULL REFERENCES inventory_management.stocktake(id),
    audit_log_id bigint NOT NULL REFERENCES inventory_management.inventory_audit_log(id),
    PRIMARY KEY (stocktake_id, audit_log_id)
);

-- Allows tracking of when sales were made and by which staff member.
CREATE TABLE IF NOT EXISTS inventory_management.sale (
    id bigserial NOT NULL PRIMARY KEY,
    location_id bigint NOT NULL,
    staff_member_id bigint NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL
);

-- Links the sale table to the audit logs, allowing the app to query what ingredients were expended for a particular sale
CREATE TABLE IF NOT EXISTS inventory_management.sale_audit_log (
    sale_id bigint NOT NULL REFERENCES inventory_management.sale(id),
    audit_log_id bigint NOT NULL REFERENCES inventory_management.inventory_audit_log(id),
    PRIMARY KEY (sale_id, audit_log_id)
);

-- Tracks each individual menu item that was sold during a particular sale, including quantity of that item
CREATE TABLE IF NOT EXISTS inventory_management.sale_item (
    id bigserial NOT NULL PRIMARY KEY,
    sale_id bigint NOT NULL REFERENCES inventory_management.sale(id),
    recipe_id bigint NOT NULL,
    location_id bigint NOT NULL,
    quantity int NOT NULL,
    total_price decimal NOT NULL,
    created_by varchar(255) NOT NULL,
    created_at timestamp NOT NULL,
    updated_by varchar(255) NOT NULL,
    updated_at timestamp NOT NULL,
    FOREIGN KEY (recipe_id, location_id) REFERENCES inventory_management.menu_item(recipe_id, location_id)
);

-- Tracks what modifiers were used for a particular sale item, so that the cost of the modifier can be added to the total price
CREATE TABLE IF NOT EXISTS inventory_management.sale_item_modifiers (
    sale_item_id bigint NOT NULL REFERENCES inventory_management.sale_item(id),
    modifier_id bigint NOT NULL REFERENCES inventory_management.modifier(id),
    PRIMARY KEY (sale_item_id, modifier_id)
);
