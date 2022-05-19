INSERT INTO category (id, description) VALUES(1, 'Comic Books');
INSERT INTO category (id, description) VALUES(2, 'Movies');
INSERT INTO category (id, description) VALUES(3, 'Books');

INSERT INTO public.supplier (id, "name") VALUES(1, 'Panini Commics');
INSERT INTO public.supplier (id, "name") VALUES(2, 'Amazon');

INSERT INTO public.product (id, "name", quantity_available, category_id, supplier_id, created_at) VALUES(1, 'Crise nas Infinitas terras', 10, 1, 1, current_timestamp);
INSERT INTO public.product (id, "name", quantity_available, category_id, supplier_id, created_at) VALUES(2, 'Interestelar', 5, 2, 2, current_timestamp);
INSERT INTO public.product (id, "name", quantity_available, category_id, supplier_id, created_at) VALUES(3, 'Harry Potter e a Pedra Filosofal', 3, 3, 2, current_timestamp);
SELECT setval('hibernate_sequence', 4, true);
