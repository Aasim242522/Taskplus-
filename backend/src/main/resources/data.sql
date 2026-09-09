-- Initial Seed Data: System Categories
MERGE INTO categories (id, user_id, name, color_hex, icon, is_system, created_at)
KEY(name)
VALUES 
    ('11111111-1111-1111-1111-111111111101', NULL, 'Work', '#4F46E5', 'briefcase', TRUE, CURRENT_TIMESTAMP),
    ('11111111-1111-1111-1111-111111111102', NULL, 'Personal', '#10B981', 'user', TRUE, CURRENT_TIMESTAMP),
    ('11111111-1111-1111-1111-111111111103', NULL, 'Fitness', '#EF4444', 'activity', TRUE, CURRENT_TIMESTAMP),
    ('11111111-1111-1111-1111-111111111104', NULL, 'Study', '#F59E0B', 'book-open', TRUE, CURRENT_TIMESTAMP),
    ('11111111-1111-1111-1111-111111111105', NULL, 'Finance', '#8B5CF6', 'dollar-sign', TRUE, CURRENT_TIMESTAMP);

-- Initial Seed Data: Badges / Achievements
MERGE INTO badges (id, code, name, description, icon_url, badge_type, criteria_metadata, points_reward, created_at)
KEY(code)
VALUES
    ('22222222-2222-2222-2222-222222222201', 'FIRST_STEP', 'First Step', 'Complete your very first task in the system', 'award', 'TASK_COUNT', '{"target": 1}', 50, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222202', 'STREAK_3', 'On a Roll', 'Maintain a consecutive 3-day completion streak', 'zap', 'STREAK', '{"target": 3}', 100, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222203', 'STREAK_7', 'Week Warrior', 'Maintain a consecutive 7-day completion streak', 'flame', 'STREAK', '{"target": 7}', 250, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222204', 'STREAK_30', 'Monthly Champion', 'Maintain a consecutive 30-day streak of productivity', 'crown', 'STREAK', '{"target": 30}', 1000, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222205', 'TASKS_10', 'Getting Serious', 'Successfully complete 10 tasks', 'check-circle', 'TASK_COUNT', '{"target": 10}', 150, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222206', 'TASKS_50', 'Centurion in Progress', 'Successfully complete 50 tasks', 'shield', 'TASK_COUNT', '{"target": 50}', 500, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222207', 'TASKS_100', 'Task Titan', 'Reach 100 total completed tasks', 'star', 'TASK_COUNT', '{"target": 100}', 1000, CURRENT_TIMESTAMP),
    ('22222222-2222-2222-2222-222222222208', 'FITNESS_5', 'Active Mind & Body', 'Complete 5 fitness tasks', 'heart', 'CATEGORY_MASTERY', '{"category": "Fitness", "target": 5}', 150, CURRENT_TIMESTAMP);
