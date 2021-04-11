INSERT INTO product 
    (id, name, price, weight) 
VALUES 
    (1, 'phone', '500', '1000');

INSERT INTO inventory
    (id, product_id, quantity)
VALUES 
    (1, 1, 5);