--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES ('R001','ADMIN');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (2,'jnempu','$2a$10$jFLaYgH.TqmAgZnOy2c1pO0tflp1/75/E6mb0wyQuSYmWetecyeAe','R001');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;