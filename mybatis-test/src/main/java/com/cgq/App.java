package com.cgq;

import com.cgq.mapper.BlogMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.builder.xml.XMLIncludeTransformer;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public class App {

    public static void main(String[] args) throws IOException {
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sessionFactory = builder.build(Resources.getResourceAsReader("mybatis-config.xml"));
        SqlSession sqlSession = sessionFactory.openSession();
        BlogMapper mapper = sqlSession.getMapper(BlogMapper.class);
        int count = mapper.count();
        System.out.println(count);

        XPathParser parser = new XPathParser("<select id=\"count\" resultType=\"int\">\n" +
                "        select <include refid=\"123\"/> from time_zone_name\n" +
                "    </select>",false,new Properties(),new XMLMapperEntityResolver());
        XNode xNode = parser.evalNode("select");
        NodeList childNodes = xNode.getNode().getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            System.out.print(item.getTextContent() + "\t");
            System.out.println(item.getNodeName());
        }


    }
}
