INSERT INTO groups (id, name)
VALUES ('66666666-6666-4666-6666-666666666666', 'Java group');

INSERT INTO teachers (id, first_name, last_name)
VALUES ('88888888-8888-4888-8888-888888888888', 'Андрей', 'Андреев');

INSERT INTO courses (id, name, description)
VALUES ('aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa', 'Java', 'Backend development with Java');

INSERT INTO schedules (id, group_id, teacher_id, course_id, start_date, end_date)
VALUES
    (
        '11111111-1111-4111-1111-111111111111',
        '66666666-6666-4666-6666-666666666666',
        '88888888-8888-4888-8888-888888888888',
        'aaaaaaaa-aaaa-4aaa-aaaa-aaaaaaaaaaaa',
        '2026-10-22 10:30:00',
        '2026-10-22 12:00:00'
    );