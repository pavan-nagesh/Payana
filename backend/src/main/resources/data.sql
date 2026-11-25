insert into stops(id,name,latitude,longitude,mode) values
('majestic','Kempegowda Bus Station',12.9767,77.5713,'bus'),
('silk-board','Silk Board',12.9173,77.6228,'bus'),
('ypr','Yeshwanthpur',13.0285,77.5547,'train'),
('airport','Kempegowda International Airport',13.1986,77.7066,'flight')
on conflict (id) do nothing;
