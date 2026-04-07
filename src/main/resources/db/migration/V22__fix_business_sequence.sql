SELECT setval(
    'businesses_id_seq',
    (SELECT MAX(id) FROM businesses)
);